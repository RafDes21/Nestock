package com.rafdev.nestock.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafdev.nestock.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppAuthViewModel @Inject constructor(
    authRepository: AuthRepository
) : ViewModel() {

    val isLoggedIn = authRepository.authStateFlow()
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), authRepository.currentUser != null)
}
