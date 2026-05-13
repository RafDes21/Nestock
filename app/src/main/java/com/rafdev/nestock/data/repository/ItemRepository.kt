package com.rafdev.nestock.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rafdev.nestock.data.model.Item
import com.rafdev.nestock.data.model.ShoppingEntry
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface ItemRepository {
    fun observeItems(householdId: String): Flow<List<Item>>
    suspend fun getItem(householdId: String, itemId: String): Item?
    suspend fun addItem(householdId: String, item: Item): Result<String>
    suspend fun updateItem(householdId: String, item: Item): Result<Unit>
    suspend fun updateQuantity(householdId: String, itemId: String, delta: Double): Result<Unit>
    suspend fun deleteItem(householdId: String, itemId: String): Result<Unit>
}

class ItemRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ItemRepository {

    private fun itemsCol(householdId: String) =
        firestore.collection("households/$householdId/items")

    override fun observeItems(householdId: String): Flow<List<Item>> = callbackFlow {
        val listener = itemsCol(householdId).addSnapshotListener { snap, _ ->
            trySend(snap?.toObjects(Item::class.java) ?: emptyList())
        }
        awaitClose { listener.remove() }
    }

    override suspend fun getItem(householdId: String, itemId: String): Item? =
        itemsCol(householdId).document(itemId).get().await().toObject(Item::class.java)

    override suspend fun addItem(householdId: String, item: Item): Result<String> = runCatching {
        val uid = auth.currentUser!!.uid
        val lowStock = item.quantity <= item.minQuantity
        val newItem = item.copy(updatedBy = uid, updatedAt = Timestamp.now(), isLowStock = lowStock)
        val ref = itemsCol(householdId).add(newItem).await()
        if (lowStock) addToShoppingList(householdId, newItem.copy(id = ref.id), uid)
        ref.id
    }

    override suspend fun updateItem(householdId: String, item: Item): Result<Unit> = runCatching {
        val uid = auth.currentUser!!.uid
        val lowStock = item.quantity <= item.minQuantity
        val updated = item.copy(updatedBy = uid, updatedAt = Timestamp.now(), isLowStock = lowStock)
        itemsCol(householdId).document(item.id).set(updated).await()
        if (lowStock) addToShoppingList(householdId, updated, uid)
        else removeFromShoppingList(householdId, item.id)
    }

    override suspend fun updateQuantity(householdId: String, itemId: String, delta: Double): Result<Unit> = runCatching {
        val item = getItem(householdId, itemId) ?: throw Exception("Insumo no encontrado")
        val newQty = maxOf(0.0, item.quantity + delta)
        updateItem(householdId, item.copy(quantity = newQty)).getOrThrow()
    }

    override suspend fun deleteItem(householdId: String, itemId: String): Result<Unit> = runCatching {
        removeFromShoppingList(householdId, itemId)
        itemsCol(householdId).document(itemId).delete().await()
    }

    private suspend fun addToShoppingList(householdId: String, item: Item, uid: String) {
        val entry = ShoppingEntry(
            itemId = item.id,
            itemName = item.name,
            quantityNeeded = maxOf(1.0, item.optimalQuantity - item.quantity),
            unit = item.unit,
            addedBy = uid
        )
        firestore.collection("households/$householdId/shoppingList")
            .document(item.id)
            .set(entry)
            .await()
    }

    private suspend fun removeFromShoppingList(householdId: String, itemId: String) {
        firestore.collection("households/$householdId/shoppingList").document(itemId).delete().await()
    }
}
