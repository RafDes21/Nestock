package com.rafdev.nestock.ui.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafdev.nestock.data.repository.AuthRepository
import com.rafdev.nestock.di.AppStateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val appState: AppStateHolder
) : ViewModel() {

    val currentUser get() = appState.currentUser.value
    val isEmailUser get() = appState.currentUser.value?.isEmailProvider == true

    var notificationsEnabled by mutableStateOf(true)
    var darkModeEnabled      by mutableStateOf(false)

    var isDeleting     by mutableStateOf(false)
    var deleteError    by mutableStateOf<String?>(null)
    var deletePassword by mutableStateOf("")

    fun logout(onLogout: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()  // limpia Firebase Auth + DataStore
            onLogout()
        }
    }

    fun deleteAccount(onDeleted: () -> Unit) {
        isDeleting = true
        deleteError = null
        viewModelScope.launch {
            authRepository.deleteAccount(password = deletePassword)
                .onSuccess {
                    onDeleted()  // DataStore limpiado por el repo
                }
                .onFailure { e ->
                    val msg = e.message ?: ""
                    deleteError = when {
                        msg.contains("password", ignoreCase = true) ||
                        msg.contains("credential", ignoreCase = true) ||
                        msg.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) ->
                            "Contraseña incorrecta"
                        msg.contains("network", ignoreCase = true) ->
                            "Sin conexión a internet"
                        else -> "No se pudo eliminar la cuenta. Intentá de nuevo."
                    }
                    isDeleting = false
                }
        }
    }
}
