package com.rafdev.nestock.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.rafdev.nestock.data.model.Category
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface CategoryRepository {
    fun observeCategories(householdId: String): Flow<List<Category>>
    suspend fun addCategory(householdId: String, category: Category): Result<Unit>
    suspend fun updateCategory(householdId: String, category: Category): Result<Unit>
    suspend fun deleteCategory(householdId: String, categoryId: String): Result<Unit>
}

class CategoryRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CategoryRepository {

    private fun col(householdId: String) =
        firestore.collection("households/$householdId/categories")

    override fun observeCategories(householdId: String): Flow<List<Category>> = callbackFlow {
        val listener = col(householdId).addSnapshotListener { snap, _ ->
            trySend(snap?.toObjects(Category::class.java) ?: emptyList())
        }
        awaitClose { listener.remove() }
    }

    override suspend fun addCategory(householdId: String, category: Category): Result<Unit> =
        runCatching { col(householdId).add(category).await(); Unit }

    override suspend fun updateCategory(householdId: String, category: Category): Result<Unit> =
        runCatching { col(householdId).document(category.id).set(category).await() }

    override suspend fun deleteCategory(householdId: String, categoryId: String): Result<Unit> =
        runCatching { col(householdId).document(categoryId).delete().await() }
}
