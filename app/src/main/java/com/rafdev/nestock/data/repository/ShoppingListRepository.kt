package com.rafdev.nestock.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rafdev.nestock.data.model.ShoppingEntry
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface ShoppingListRepository {
    fun observeShoppingList(householdId: String): Flow<List<ShoppingEntry>>
    suspend fun markAsPurchased(householdId: String, entryId: String): Result<Unit>
    suspend fun removeEntry(householdId: String, entryId: String): Result<Unit>
    suspend fun addEntry(householdId: String, entry: ShoppingEntry): Result<Unit>
}

class ShoppingListRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ShoppingListRepository {

    private fun col(householdId: String) =
        firestore.collection("households/$householdId/shoppingList")

    override fun observeShoppingList(householdId: String): Flow<List<ShoppingEntry>> = callbackFlow {
        val listener = col(householdId).addSnapshotListener { snap, _ ->
            trySend(snap?.toObjects(ShoppingEntry::class.java) ?: emptyList())
        }
        awaitClose { listener.remove() }
    }

    override suspend fun markAsPurchased(householdId: String, entryId: String): Result<Unit> =
        runCatching { col(householdId).document(entryId).update("isPurchased", true).await() }

    override suspend fun removeEntry(householdId: String, entryId: String): Result<Unit> =
        runCatching { col(householdId).document(entryId).delete().await() }

    override suspend fun addEntry(householdId: String, entry: ShoppingEntry): Result<Unit> =
        runCatching { col(householdId).add(entry).await() }
}
