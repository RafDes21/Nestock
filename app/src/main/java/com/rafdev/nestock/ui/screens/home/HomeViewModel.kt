package com.rafdev.nestock.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.rafdev.nestock.data.model.Category
import com.rafdev.nestock.data.model.Household
import com.rafdev.nestock.data.model.Item
import com.rafdev.nestock.data.repository.CategoryRepository
import com.rafdev.nestock.data.repository.HouseholdRepository
import com.rafdev.nestock.data.repository.ItemRepository
import com.rafdev.nestock.di.AppStateHolder
import com.rafdev.nestock.domain.usecase.JoinHouseholdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val householdRepository: HouseholdRepository,
    private val itemRepository: ItemRepository,
    private val categoryRepository: CategoryRepository,
    private val joinHousehold: JoinHouseholdUseCase,
    private val appState: AppStateHolder
) : ViewModel() {

    val householdId: StateFlow<String?> = appState.currentHouseholdId
    val currentUser get() = auth.currentUser

    private val _isLoadingHousehold = MutableStateFlow(true)
    val isLoadingHousehold: StateFlow<Boolean> = _isLoadingHousehold.asStateFlow()

    private val _household = MutableStateFlow<Household?>(null)
    val household: StateFlow<Household?> = _household.asStateFlow()

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    val lowStockItems: StateFlow<List<Item>> = _items
        .map { it.filter { item -> item.isLowStock } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cantidad de ítems por categoryId
    val categorySummary: StateFlow<Map<String, Int>> = _items
        .map { items -> items.groupBy { it.categoryId }.mapValues { it.value.size } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    var joinCode by mutableStateOf("")
    var joinError by mutableStateOf<String?>(null)
    var isJoining by mutableStateOf(false)

    init {
        viewModelScope.launch { initHousehold() }
    }

    private suspend fun initHousehold() {
        if (auth.currentUser == null) { _isLoadingHousehold.value = false; return }
        appState.loaded.filter { it }.first()
        _isLoadingHousehold.value = false
        appState.currentHouseholdId.collectLatest { hid ->
            if (hid != null) observeData(hid)
        }
    }

    private fun observeData(householdId: String) {
        viewModelScope.launch {
            householdRepository.observeHousehold(householdId)
                .catch { }
                .collect { _household.value = it }
        }
        viewModelScope.launch {
            itemRepository.observeItems(householdId)
                .catch { }
                .collect { _items.value = it }
        }
        viewModelScope.launch {
            categoryRepository.observeCategories(householdId)
                .catch { }
                .collect { _categories.value = it }
        }
    }

    fun createHousehold(name: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            householdRepository.createHousehold(name)
                .onSuccess { id -> appState.setHouseholdId(id); onSuccess() }
        }
    }

    fun joinWithCode(onSuccess: () -> Unit) {
        joinError = null
        isJoining = true
        viewModelScope.launch {
            joinHousehold(joinCode.trim())
                .onSuccess { id -> appState.setHouseholdId(id); onSuccess() }
                .onFailure { joinError = "Código inválido o no encontrado ${it.message}" }
            isJoining = false
        }
    }

    fun signOut(onDone: () -> Unit) {
        appState.clearHouseholdId()
        auth.signOut()
        onDone()
    }
}
