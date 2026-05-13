package com.rafdev.nestock.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.rafdev.nestock.data.model.Household
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface HouseholdRepository {
    fun observeHousehold(householdId: String): Flow<Household?>
    suspend fun createHousehold(name: String): Result<String>
    suspend fun joinHousehold(inviteCode: String): Result<String>
    suspend fun getUserHouseholds(userId: String): List<Household>
    suspend fun getFirstHouseholdId(userId: String): String?
    suspend fun regenerateInviteCode(householdId: String): Result<String>
}

class HouseholdRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : HouseholdRepository {

    private val households = firestore.collection("households")
    private val users      = firestore.collection("users")

    override fun observeHousehold(householdId: String): Flow<Household?> = callbackFlow {
        val listener = households.document(householdId).addSnapshotListener { snap, _ ->
            trySend(snap?.toObject(Household::class.java))
        }
        awaitClose { listener.remove() }
    }

    override suspend fun createHousehold(name: String): Result<String> = runCatching {
        val uid  = auth.currentUser!!.uid
        val code = generateCode()
        val household = Household(
            name       = name,
            inviteCode = code,
            createdBy  = uid,
            members    = mapOf(uid to "owner")
        )
        val ref = households.add(household).await()
        users.document(uid).update("householdIds", FieldValue.arrayUnion(ref.id)).await()
        seedDefaultCategories(ref.id)
        ref.id
    }

    override suspend fun joinHousehold(inviteCode: String): Result<String> = runCatching {
        val uid  = auth.currentUser!!.uid
        val code = inviteCode.trim().uppercase()

        val query = households.whereEqualTo("inviteCode", code).get().await()
        if (query.isEmpty) throw Exception("Código '$code' no encontrado")
        val householdId = query.documents.first().id

        households.document(householdId).update("members.$uid", "member").await()
        users.document(uid).update("householdIds", FieldValue.arrayUnion(householdId)).await()
        householdId
    }

    override suspend fun getUserHouseholds(userId: String): List<Household> {
        val userDoc = users.document(userId).get().await()
        val ids = userDoc.get("householdIds") as? List<*> ?: return emptyList()
        return ids.filterIsInstance<String>().mapNotNull { id ->
            households.document(id).get().await().toObject(Household::class.java)
        }
    }

    // Solo lee el documento del usuario — sin cargar el hogar completo
    override suspend fun getFirstHouseholdId(userId: String): String? {
        val userDoc = users.document(userId).get().await()
        val ids = userDoc.get("householdIds") as? List<*> ?: return null
        return ids.filterIsInstance<String>().firstOrNull()
    }

    override suspend fun regenerateInviteCode(householdId: String): Result<String> = runCatching {
        val newCode = generateCode()
        households.document(householdId).update("inviteCode", newCode).await()
        newCode
    }

    private suspend fun seedDefaultCategories(householdId: String) {
        val col = firestore.collection("households/$householdId/categories")
        listOf(
            Triple("Alimentos",    "🥦", "#2D6A4F"),
            Triple("Limpieza",     "🧴", "#40916C"),
            Triple("Higiene",      "🧼", "#52B788"),
            Triple("Medicamentos", "💊", "#F4845F")
        ).forEach { (name, icon, color) ->
            col.add(mapOf("name" to name, "icon" to icon, "color" to color)).await()
        }
    }

    private fun generateCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6).map { chars.random() }.joinToString("")
    }
}
