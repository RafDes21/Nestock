package com.rafdev.nestock.domain.usecase

import com.rafdev.nestock.data.repository.ItemRepository
import javax.inject.Inject

class UpdateItemQuantityUseCase @Inject constructor(
    private val itemRepository: ItemRepository
) {
    suspend operator fun invoke(householdId: String, itemId: String, delta: Double): Result<Unit> =
        itemRepository.updateQuantity(householdId, itemId, delta)
}
