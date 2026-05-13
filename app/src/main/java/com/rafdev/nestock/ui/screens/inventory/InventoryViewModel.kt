package com.rafdev.nestock.ui.screens.inventory

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafdev.nestock.data.model.Category
import com.rafdev.nestock.data.model.Item
import com.rafdev.nestock.data.repository.CategoryRepository
import com.rafdev.nestock.data.repository.ItemRepository
import com.rafdev.nestock.di.AppStateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val categoryRepository: CategoryRepository,
    private val appState: AppStateHolder
) : ViewModel() {

    private val _allItems = MutableStateFlow<List<Item>>(emptyList())
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    var searchQuery by mutableStateOf("")
    var selectedCategory by mutableStateOf<Category?>(null)

    val filteredItems: StateFlow<List<Item>> = combine(
        _allItems,
        snapshotFlow { searchQuery },
        snapshotFlow { selectedCategory }
    ) { items, query, cat ->
        items
            .filter { if (cat != null) it.categoryId == cat.id else true }
            .filter { if (query.isBlank()) true else it.name.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            appState.currentHouseholdId.collectLatest { hid ->
                if (hid != null) {
                    launch {
                        itemRepository.observeItems(hid).collect { _allItems.value = it }
                    }
                    launch {
                        categoryRepository.observeCategories(hid).collect { _categories.value = it }
                    }
                }
            }
        }
    }
}
