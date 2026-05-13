package com.rafdev.nestock.ui.screens.household

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafdev.nestock.data.repository.HouseholdRepository
import com.rafdev.nestock.di.AppStateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HouseholdSetupViewModel @Inject constructor(
    private val householdRepository: HouseholdRepository,
    private val appState: AppStateHolder
) : ViewModel() {

    var householdName by mutableStateOf("")
    var inviteCode    by mutableStateOf("")
    var isLoading     by mutableStateOf(false)
    var createError   by mutableStateOf<String?>(null)
    var joinError     by mutableStateOf<String?>(null)

    fun createHousehold(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true; createError = null
            householdRepository.createHousehold(householdName.trim())
                .onSuccess { id ->
                    appState.setHouseholdId(id)
                    onSuccess()
                }
                .onFailure { createError = "No se pudo crear el hogar. Intentá de nuevo." }
            isLoading = false
        }
    }

    fun joinHousehold(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true; joinError = null
            householdRepository.joinHousehold(inviteCode.trim())
                .onSuccess { id ->
                    appState.setHouseholdId(id)
                    onSuccess()
                }
                .onFailure { e ->
                    joinError = if (e.message?.contains("no encontrado") == true)
                        "Código inválido. Verificá que esté escrito correctamente."
                    else
                        "No se pudo unir al hogar. Intentá de nuevo."
                }
            isLoading = false
        }
    }
}
