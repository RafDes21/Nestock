package com.rafdev.nestock.ui.screens.shopping

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafdev.nestock.data.model.Item
import com.rafdev.nestock.data.model.ShoppingEntry
import com.rafdev.nestock.data.repository.ItemRepository
import com.rafdev.nestock.data.repository.ShoppingListRepository
import com.rafdev.nestock.di.AppStateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

data class ShoppingItemUiState(
    val entry: ShoppingEntry,
    val currentQty: Double = 0.0,
    val minQty: Double = 1.0
) {
    val isUrgent: Boolean get() = currentQty == 0.0
    val toBuy: Double get() = max(1.0, minQty - currentQty)
}

@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    private val shoppingListRepository: ShoppingListRepository,
    private val itemRepository: ItemRepository,
    private val appState: AppStateHolder
) : ViewModel() {

    private val _entries = MutableStateFlow<List<ShoppingEntry>>(emptyList())
    private val _items = MutableStateFlow<List<Item>>(emptyList())

    // Dialog state para confirmar compra
    var purchasingEntry by mutableStateOf<ShoppingItemUiState?>(null)
    var purchaseQtyInput by mutableStateOf("")

    private val combined: StateFlow<List<ShoppingItemUiState>> = combine(_entries, _items) { entries, items ->
        entries.map { entry ->
            val item = items.firstOrNull { it.id == entry.itemId }
            ShoppingItemUiState(
                entry = entry,
                currentQty = item?.quantity ?: 0.0,
                minQty = item?.minQuantity ?: 1.0
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val urgent: StateFlow<List<ShoppingItemUiState>> = combined
        .map { it.filter { s -> s.isUrgent } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStock: StateFlow<List<ShoppingItemUiState>> = combined
        .map { it.filter { s -> !s.isUrgent } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            appState.currentHouseholdId.collectLatest { hid ->
                if (hid != null) {
                    launch { shoppingListRepository.observeShoppingList(hid).collect { _entries.value = it } }
                    launch { itemRepository.observeItems(hid).collect { _items.value = it } }
                }
            }
        }
    }

    fun openPurchaseDialog(state: ShoppingItemUiState) {
        purchasingEntry = state
        purchaseQtyInput = state.toBuy.fmt()
    }

    fun confirmPurchase() {
        val state = purchasingEntry ?: return
        val qty = purchaseQtyInput.toDoubleOrNull() ?: return
        viewModelScope.launch {
            val hid = appState.currentHouseholdId.value ?: return@launch
            itemRepository.updateQuantity(hid, state.entry.itemId, qty)
            purchasingEntry = null
        }
    }

    fun remove(entryId: String) {
        viewModelScope.launch {
            val hid = appState.currentHouseholdId.value ?: return@launch
            shoppingListRepository.removeEntry(hid, entryId)
        }
    }

    fun shareText(): String {
        val allPending = urgent.value + lowStock.value
        return "Lista de compras — Nestock\n\n" +
            allPending.joinToString("\n") { s ->
                "• ${s.entry.itemName} (${s.toBuy.fmt()} ${s.entry.unit})"
            }
    }

    private fun Double.fmt() = if (this == toLong().toDouble()) toLong().toString() else toString()
}
