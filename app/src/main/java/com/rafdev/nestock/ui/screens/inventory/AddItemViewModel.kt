package com.rafdev.nestock.ui.screens.inventory

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
class AddItemViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val categoryRepository: CategoryRepository,
    private val appState: AppStateHolder
) : ViewModel() {

    var isEditMode      by mutableStateOf(false)
    var editItemId      by mutableStateOf("")

    var name            by mutableStateOf("")
    var selectedCategoryId by mutableStateOf("")
    var barcode         by mutableStateOf("")
    var quantity        by mutableStateOf("0")
    var unit            by mutableStateOf("und")
    var minQuantity     by mutableStateOf("1")
    var optimalQuantity by mutableStateOf("5")
    var isLoading       by mutableStateOf(false)
    var error           by mutableStateOf<String?>(null)

    val units = listOf("und", "kg", "lt", "g", "ml", "rollos", "pares", "cajas", "bolsas")

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    init {
        viewModelScope.launch {
            appState.currentHouseholdId.collectLatest { hid ->
                if (hid != null) {
                    categoryRepository.observeCategories(hid).collect { _categories.value = it }
                }
            }
        }
    }

    fun loadForEdit(itemId: String) {
        isEditMode = true
        editItemId = itemId
        viewModelScope.launch {
            val hid = appState.currentHouseholdId.value ?: return@launch
            val item = itemRepository.getItem(hid, itemId) ?: return@launch
            name = item.name
            selectedCategoryId = item.categoryId
            barcode = item.barcode ?: ""
            quantity = item.quantity.fmt()
            unit = item.unit
            minQuantity = item.minQuantity.fmt()
            optimalQuantity = item.optimalQuantity.fmt()
        }
    }

    fun onBarcodeScanned(code: String) { barcode = code }

    fun save(onSuccess: () -> Unit) {
        if (name.isBlank()) { error = "Ingresá el nombre del insumo"; return }
        if (selectedCategoryId.isEmpty()) { error = "Seleccioná una categoría"; return }
        viewModelScope.launch {
            isLoading = true; error = null
            val hid = appState.currentHouseholdId.value
            if (hid == null) { error = "No hay un hogar seleccionado"; isLoading = false; return@launch }
            val item = Item(
                id              = editItemId,
                name            = name.trim(),
                categoryId      = selectedCategoryId,
                barcode         = barcode.ifBlank { null },
                quantity        = quantity.toDoubleOrNull() ?: 0.0,
                minQuantity     = minQuantity.toDoubleOrNull() ?: 1.0,
                optimalQuantity = optimalQuantity.toDoubleOrNull() ?: 5.0,
                unit            = unit
            )
            val result = if (isEditMode) {
                itemRepository.updateItem(hid, item)
            } else {
                itemRepository.addItem(hid, item).map { }
            }
            result.onSuccess { onSuccess() }.onFailure { error = it.message }
            isLoading = false
        }
    }

    private fun Double.fmt() = if (this == toLong().toDouble()) toLong().toString() else toString()
}
