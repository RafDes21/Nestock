package com.rafdev.nestock.ui.screens.household

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.rafdev.nestock.data.model.Category
import com.rafdev.nestock.data.model.Household
import com.rafdev.nestock.data.repository.CategoryRepository
import com.rafdev.nestock.data.repository.HouseholdRepository
import com.rafdev.nestock.di.AppStateHolder
import com.rafdev.nestock.domain.usecase.JoinHouseholdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HouseholdViewModel @Inject constructor(
    private val householdRepository: HouseholdRepository,
    private val categoryRepository: CategoryRepository,
    private val joinHousehold: JoinHouseholdUseCase,
    private val appState: AppStateHolder,
    private val auth: FirebaseAuth
) : ViewModel() {

    val householdId: StateFlow<String?> = appState.currentHouseholdId

    private val _household = MutableStateFlow<Household?>(null)
    val household: StateFlow<Household?> = _household.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    val currentUserId get() = auth.currentUser?.uid ?: ""

    var joinCode by mutableStateOf("")
    var joinError by mutableStateOf<String?>(null)

    // Dialog state — categorías
    var showAddCategoryDialog by mutableStateOf(false)
    var showEditCategoryDialog by mutableStateOf(false)
    var showDeleteCategoryDialog by mutableStateOf(false)
    var selectedCategory by mutableStateOf<Category?>(null)
    var editCategoryName by mutableStateOf("")
    var editCategoryIcon by mutableStateOf("📦")
    var categoryError by mutableStateOf<String?>(null)

    val MAX_CATEGORIES = 10

    init {
        viewModelScope.launch {
            appState.currentHouseholdId.collectLatest { hid ->
                if (hid != null) {
                    launch {
                        householdRepository.observeHousehold(hid).collect { _household.value = it }
                    }
                    launch {
                        categoryRepository.observeCategories(hid).collect {
                            _categories.value = it
                        }
                    }
                }
            }
        }
    }

    fun joinWithCode(onSuccess: () -> Unit) {
        joinError = null
        viewModelScope.launch {
            joinHousehold(joinCode)
                .onSuccess { id -> appState.setHouseholdId(id); onSuccess() }
                .onFailure { joinError = it.message }
        }
    }

    // ── Categorías ────────────────────────────────────────────────────────────

    fun openAddCategory() {
        editCategoryName = ""
        editCategoryIcon = "📦"
        categoryError = null
        showAddCategoryDialog = true
    }

    fun openEditCategory(category: Category) {
        selectedCategory = category
        editCategoryName = category.name
        editCategoryIcon = category.icon
        categoryError = null
        showEditCategoryDialog = true
    }

    fun openDeleteCategory(category: Category) {
        selectedCategory = category
        showDeleteCategoryDialog = true
    }

    fun saveNewCategory() {
        if (editCategoryName.isBlank()) { categoryError = "Ingresá un nombre"; return }
        if (_categories.value.size >= MAX_CATEGORIES) {
            categoryError = "Límite de $MAX_CATEGORIES categorías alcanzado"
            return
        }
        val hid = appState.currentHouseholdId.value ?: return
        viewModelScope.launch {
            categoryRepository.addCategory(hid, Category(name = editCategoryName.trim(), icon = editCategoryIcon))
                .onSuccess { showAddCategoryDialog = false; categoryError = null }
                .onFailure { categoryError = it.message }
        }
    }

    fun saveEditCategory() {
        val cat = selectedCategory ?: return
        if (editCategoryName.isBlank()) { categoryError = "Ingresá un nombre"; return }
        val hid = appState.currentHouseholdId.value ?: return
        viewModelScope.launch {
            categoryRepository.updateCategory(hid, cat.copy(name = editCategoryName.trim(), icon = editCategoryIcon))
                .onSuccess { showEditCategoryDialog = false; categoryError = null }
                .onFailure { categoryError = it.message }
        }
    }

    fun confirmDeleteCategory() {
        val cat = selectedCategory ?: return
        val hid = appState.currentHouseholdId.value ?: return
        viewModelScope.launch {
            categoryRepository.deleteCategory(hid, cat.id)
                .onSuccess { showDeleteCategoryDialog = false }
        }
    }

}
