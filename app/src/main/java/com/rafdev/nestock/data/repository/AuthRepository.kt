package com.rafdev.nestock.data.repository

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.rafdev.nestock.data.model.User
import com.rafdev.nestock.data.preferences.LocalUser
import com.rafdev.nestock.data.preferences.UserPreferencesDataStore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface AuthRepository {
    val currentUser: FirebaseUser?
    fun authStateFlow(): Flow<FirebaseUser?>
    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser>
    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser>
    suspend fun register(email: String, password: String, displayName: String): Result<FirebaseUser>
    suspend fun signOut()
    suspend fun deleteAccount(password: String = ""): Result<Unit>
}

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val prefs: UserPreferencesDataStore
) : AuthRepository {

    override val currentUser: FirebaseUser? get() = auth.currentUser

    override fun authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> =
        runCatching {
            auth.signInWithEmailAndPassword(email, password).await().user!!
        }.onSuccess { user ->
            prefs.saveUser(user.toLocalUser())
        }

    override suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> =
        runCatching {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user!!
            if (result.additionalUserInfo?.isNewUser == true) createUserProfile(user)
            user
        }.onSuccess { user ->
            prefs.saveUser(user.toLocalUser(isEmailProvider = false))
        }

    override suspend fun register(email: String, password: String, displayName: String): Result<FirebaseUser> =
        runCatching {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user!!
            createUserProfile(user, displayName)
            user
        }
        // Sin saveUser — el usuario debe iniciar sesión explícitamente después de registrarse

    override suspend fun signOut() {
        auth.signOut()
        prefs.clear()
    }

    override suspend fun deleteAccount(password: String): Result<Unit> = runCatching {
        val user = auth.currentUser!!
        val uid = user.uid

        val isEmailUser = user.providerData.any { it.providerId == "password" }
        if (isEmailUser && password.isNotBlank()) {
            val credential = EmailAuthProvider.getCredential(user.email!!, password)
            user.reauthenticate(credential).await()
        }

        val idsFromUser = runCatching {
            val userDoc = firestore.collection("users").document(uid).get().await()
            (userDoc.get("householdIds") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
        }.getOrElse { emptyList() }

        val idsFromQuery = runCatching {
            firestore.collection("households")
                .whereEqualTo("createdBy", uid).get().await()
                .documents.map { it.id }
        }.getOrElse { emptyList() }

        val allIds = (idsFromUser + idsFromQuery).distinct()

        allIds.forEach { hid ->
            runCatching {
                val snap = firestore.collection("households").document(hid).get().await()
                if (!snap.exists()) return@runCatching

                val createdBy = snap.getString("createdBy")
                @Suppress("UNCHECKED_CAST")
                val membersMap = snap.get("members") as? Map<String, String> ?: emptyMap()
                val isOwner = createdBy == uid || membersMap[uid] == "owner"

                if (isOwner) {
                    listOf("items", "categories", "shoppingList").forEach { sub ->
                        runCatching { deleteSubcollection("households/$hid/$sub") }
                    }
                    firestore.collection("households").document(hid).delete().await()
                } else {
                    firestore.collection("households").document(hid)
                        .update("members.$uid", FieldValue.delete())
                        .await()
                }
            }
        }

        runCatching { deleteSubcollection("notifications/$uid/feed") }
        runCatching { firestore.collection("users").document(uid).delete().await() }

        auth.currentUser!!.delete().await()
        Unit // ← fix: forzamos que el bloque retorne Unit en lugar de Void
    }.onSuccess {
        prefs.clear()
    }

    private suspend fun deleteSubcollection(path: String) {
        val docs = runCatching { firestore.collection(path).get().await() }.getOrNull() ?: return
        docs.documents.forEach { doc -> runCatching { doc.reference.delete().await() } }
    }

    private suspend fun createUserProfile(user: FirebaseUser, displayName: String? = null) {
        val doc = User(
            id = user.uid,
            displayName = displayName ?: user.displayName ?: user.email?.substringBefore("@") ?: "Usuario",
            email = user.email ?: "",
            photoUrl = user.photoUrl?.toString()
        )
        firestore.collection("users").document(user.uid).set(doc).await()
    }

    private fun FirebaseUser.toLocalUser(isEmailProvider: Boolean = providerData.any { it.providerId == "password" }) =
        LocalUser(
            uid             = uid,
            displayName     = displayName ?: email?.substringBefore("@") ?: "Usuario",
            email           = email ?: "",
            photoUrl        = photoUrl?.toString(),
            isEmailProvider = isEmailProvider
        )
}
