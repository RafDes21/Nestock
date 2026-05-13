package com.rafdev.nestock.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nestock_prefs")

data class LocalUser(
    val uid: String,
    val displayName: String,
    val email: String,
    val photoUrl: String?,
    val isEmailProvider: Boolean
)

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val HOUSEHOLD_ID_KEY  = stringPreferencesKey("current_household_id")
    private val USER_ID_KEY       = stringPreferencesKey("user_id")
    private val USER_NAME_KEY     = stringPreferencesKey("user_name")
    private val USER_EMAIL_KEY    = stringPreferencesKey("user_email")
    private val USER_PHOTO_KEY    = stringPreferencesKey("user_photo")
    private val USER_IS_EMAIL_KEY = booleanPreferencesKey("user_is_email_provider")

    val userFlow: Flow<LocalUser?> = context.dataStore.data.map { prefs ->
        val uid = prefs[USER_ID_KEY] ?: return@map null
        LocalUser(
            uid             = uid,
            displayName     = prefs[USER_NAME_KEY] ?: "",
            email           = prefs[USER_EMAIL_KEY] ?: "",
            photoUrl        = prefs[USER_PHOTO_KEY],
            isEmailProvider = prefs[USER_IS_EMAIL_KEY] ?: false
        )
    }

    val currentHouseholdId: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[HOUSEHOLD_ID_KEY] }

    suspend fun saveUser(user: LocalUser) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID_KEY]       = user.uid
            prefs[USER_NAME_KEY]     = user.displayName
            prefs[USER_EMAIL_KEY]    = user.email
            prefs[USER_IS_EMAIL_KEY] = user.isEmailProvider
            if (user.photoUrl != null) prefs[USER_PHOTO_KEY] = user.photoUrl
            else prefs.remove(USER_PHOTO_KEY)
        }
    }

    suspend fun setHouseholdId(id: String) {
        context.dataStore.edit { prefs -> prefs[HOUSEHOLD_ID_KEY] = id }
    }

    suspend fun clearHouseholdId() {
        context.dataStore.edit { prefs -> prefs.remove(HOUSEHOLD_ID_KEY) }
    }

    suspend fun clear() {
        context.dataStore.edit { prefs -> prefs.clear() }
    }
}
