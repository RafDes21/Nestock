package com.rafdev.nestock.di

import com.rafdev.nestock.data.preferences.LocalUser
import com.rafdev.nestock.data.preferences.UserPreferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppStateHolder @Inject constructor(
    private val prefs: UserPreferencesDataStore
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Se actualiza solo cuando AuthRepository escribe en DataStore
    val currentUser: StateFlow<LocalUser?> = prefs.userFlow
        .stateIn(scope, SharingStarted.Eagerly, null)

    val currentHouseholdId: StateFlow<String?> = prefs.currentHouseholdId
        .stateIn(scope, SharingStarted.Eagerly, null)

    private val _loaded = MutableStateFlow(false)
    val loaded: StateFlow<Boolean> = _loaded.asStateFlow()

    init {
        // Marca loaded cuando DataStore termina su primera lectura
        scope.launch {
            prefs.userFlow.first()
            _loaded.value = true
        }
    }

    fun setHouseholdId(id: String) {
        scope.launch { prefs.setHouseholdId(id) }
    }

    fun clearHouseholdId() {
        scope.launch { prefs.clearHouseholdId() }
    }
}
