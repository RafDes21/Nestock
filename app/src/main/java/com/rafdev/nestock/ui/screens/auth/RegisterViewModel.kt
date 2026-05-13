package com.rafdev.nestock.ui.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthException
import com.rafdev.nestock.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var name            by mutableStateOf("")
    var email           by mutableStateOf("")
    var password        by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var isLoading       by mutableStateOf(false)
    var error           by mutableStateOf<String?>(null)

    fun register(onSuccess: () -> Unit) {
        error = null
        if (password != confirmPassword) { error = "Las contraseñas no coinciden"; return }
        if (password.length < 6) { error = "La contraseña debe tener al menos 6 caracteres"; return }
        viewModelScope.launch {
            isLoading = true
            authRepository.register(email.trim(), password, name.trim())
                .onSuccess { onSuccess() }
                .onFailure { error = friendlyError(it) }
            isLoading = false
        }
    }

    private fun friendlyError(t: Throwable): String {
        val code = (t as? FirebaseAuthException)?.errorCode ?: ""
        val msg  = t.message ?: ""
        return when {
            code == "ERROR_EMAIL_ALREADY_IN_USE"
                || msg.contains("already in use", ignoreCase = true) ->
                "Ya existe una cuenta con ese email. Si la creaste con Google, iniciá sesión con Google."
            code == "ERROR_WEAK_PASSWORD"
                || msg.contains("weak", ignoreCase = true) ->
                "La contraseña es muy débil. Usá al menos 6 caracteres."
            code == "ERROR_INVALID_EMAIL"
                || msg.contains("badly formatted", ignoreCase = true) ->
                "El formato del email no es válido"
            code == "ERROR_NETWORK_REQUEST_FAILED"
                || msg.contains("network", ignoreCase = true) ->
                "Sin conexión a internet"
            else -> msg.ifBlank { "Error al registrarse" }
        }
    }
}
