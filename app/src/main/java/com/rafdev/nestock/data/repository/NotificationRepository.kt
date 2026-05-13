package com.rafdev.nestock.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.rafdev.nestock.data.model.AppNotification
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface NotificationRepository {
    fun observeNotifications(userId: String): Flow<List<AppNotification>>
    suspend fun markAsRead(userId: String, notifId: String): Result<Unit>
    suspend fun markAllAsRead(userId: String): Result<Unit>
}

class NotificationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : NotificationRepository {

    private fun col(userId: String) =
        firestore.collection("notifications/$userId/feed")

    override fun observeNotifications(userId: String): Flow<List<AppNotification>> = callbackFlow {
        val listener = col(userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snap, _ ->
                trySend(snap?.toObjects(AppNotification::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    override suspend fun markAsRead(userId: String, notifId: String): Result<Unit> =
        runCatching { col(userId).document(notifId).update("isRead", true).await() }

    override suspend fun markAllAsRead(userId: String): Result<Unit> = runCatching {
        val unread = col(userId).whereEqualTo("isRead", false).get().await()
        val batch = firestore.batch()
        unread.documents.forEach { batch.update(it.reference, "isRead", true) }
        batch.commit().await()
    }
}
