package com.rafdev.nestock.ui.screens.auth

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthException
import com.rafdev.nestock.data.repository.AuthRepository
import com.rafdev.nestock.data.repository.HouseholdRepository
import com.rafdev.nestock.di.AppStateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val householdRepository: HouseholdRepository,
    private val appState: AppStateHolder
) : ViewModel() {

    var email     by mutableStateOf("")
    var password  by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var error     by mutableStateOf<String?>(null)

    fun login(onSuccess: (hasHousehold: Boolean) -> Unit) {
        viewModelScope.launch {
            isLoading = true; error = null
            authRepository.signInWithEmail(email.trim(), password)
                .onSuccess { user ->
                    val householdId = restoreHousehold(user.uid)
                    onSuccess(householdId != null)
                }
                .onFailure { error = friendlyError(it) }
            isLoading = false
        }
    }

    fun loginWithGoogle(idToken: String, onSuccess: (hasHousehold: Boolean) -> Unit) {
        viewModelScope.launch {
            isLoading = true; error = null
            Log.d("GoogleSignIn", "Autenticando con Firebase, idToken length=${idToken.length}")
            authRepository.signInWithGoogle(idToken)
                .onSuccess { user ->
                    Log.d("GoogleSignIn", "Firebase OK → uid=${user.uid}")
                    val householdId = restoreHousehold(user.uid)
                    onSuccess(householdId != null)
                }
                .onFailure {
                    Log.e("GoogleSignIn", "Firebase falló: ${it.message}", it)
                    error = friendlyError(it)
                }
            isLoading = false
        }
    }

    private suspend fun restoreHousehold(uid: String): String? {
        val result = runCatching { householdRepository.getFirstHouseholdId(uid) }

        if (result.isFailure) {
            // Error de red — conservar lo que ya está en DataStore
            return appState.currentHouseholdId.value
        }

        val householdId = result.getOrNull()
        if (householdId != null) {
            appState.setHouseholdId(householdId)
        } else {
            // El documento del usuario confirma que no tiene ningún hogar
            appState.clearHouseholdId()
        }
        return householdId
    }

    private fun friendlyError(t: Throwable): String {
        val code = (t as? FirebaseAuthException)?.errorCode ?: ""
        val msg  = t.message ?: ""
        return when {
            code == "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL"
                || msg.contains("account-exists-with-different-credential") ->
                "Este email ya está registrado con Google. Iniciá sesión con Google."
            code == "ERROR_WRONG_PASSWORD"
                || code == "ERROR_INVALID_CREDENTIAL"
                || msg.contains("password", ignoreCase = true)
                || msg.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) ->
                "Email o contraseña incorrectos"
            code == "ERROR_USER_NOT_FOUND"
                || msg.contains("no user", ignoreCase = true) ->
                "No existe una cuenta con ese email"
            code == "ERROR_NETWORK_REQUEST_FAILED"
                || msg.contains("network", ignoreCase = true) ->
                "Sin conexión a internet"
            else -> msg.ifBlank { "Error al iniciar sesión" }
        }
    }
}
