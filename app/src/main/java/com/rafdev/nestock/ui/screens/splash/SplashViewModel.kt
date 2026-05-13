package com.rafdev.nestock.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafdev.nestock.di.AppStateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SplashDestination { NONE, HOME, HOUSEHOLD_SETUP, LOGIN }

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val appState: AppStateHolder
) : ViewModel() {

    private val _destination = MutableStateFlow(SplashDestination.NONE)
    val destination: StateFlow<SplashDestination> = _destination.asStateFlow()

    init {
        viewModelScope.launch {
            val minDelay = launch { delay(900) }
            appState.loaded.filter { it }.first()
            minDelay.join()

            _destination.value = when {
                appState.currentUser.value == null        -> SplashDestination.LOGIN
                appState.currentHouseholdId.value != null -> SplashDestination.HOME
                else                                      -> SplashDestination.HOUSEHOLD_SETUP
            }
        }
    }
}
