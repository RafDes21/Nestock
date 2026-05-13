package com.rafdev.nestock.ui.screens.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafdev.nestock.data.model.Item
import com.rafdev.nestock.data.repository.ItemRepository
import com.rafdev.nestock.di.AppStateHolder
import com.rafdev.nestock.domain.usecase.UpdateItemQuantityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemDetailViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val updateQuantity: UpdateItemQuantityUseCase,
    private val appState: AppStateHolder
) : ViewModel() {

    private val _item = MutableStateFlow<Item?>(null)
    val item: StateFlow<Item?> = _item.asStateFlow()

    var isLoading = false
    var error: String? = null

    fun loadItem(itemId: String) {
        viewModelScope.launch {
            val hid = appState.currentHouseholdId.value ?: return@launch
            itemRepository.observeItems(hid)
                .map { items -> items.firstOrNull { it.id == itemId } }
                .collect { _item.value = it }
        }
    }

    fun add(itemId: String, onDone: () -> Unit = {}) {
        modify(itemId, +1.0, onDone)
    }

    fun reduce(itemId: String, onDone: () -> Unit = {}) {
        modify(itemId, -1.0, onDone)
    }

    fun deleteItem(itemId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val hid = appState.currentHouseholdId.value ?: return@launch
            itemRepository.deleteItem(hid, itemId)
                .onSuccess { onSuccess() }
                .onFailure { error = it.message }
        }
    }

    private fun modify(itemId: String, delta: Double, onDone: () -> Unit) {
        viewModelScope.launch {
            val hid = appState.currentHouseholdId.value ?: return@launch
            updateQuantity(hid, itemId, delta)
                .onSuccess { onDone() }
                .onFailure { error = it.message }
        }
    }
}
