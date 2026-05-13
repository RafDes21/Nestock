package com.rafdev.nestock.domain.usecase

import com.rafdev.nestock.data.model.Item
import com.rafdev.nestock.data.repository.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLowStockItemsUseCase @Inject constructor(
    private val itemRepository: ItemRepository
) {
    operator fun invoke(householdId: String): Flow<List<Item>> =
        itemRepository.observeItems(householdId).map { it.filter { item -> item.isLowStock } }
}
