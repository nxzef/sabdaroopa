package com.nascriptone.siddharoopa.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nascriptone.siddharoopa.ui.screen.settings.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private companion object {
        val CURRENT_THEME = stringPreferencesKey("current_theme")
        const val TAG = "UserPreferencesRepo"
    }

    val currentTheme: Flow<Theme> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            val themeName = preferences[CURRENT_THEME]
            runCatching { Theme.valueOf(themeName ?: Theme.SYSTEM.name) }
                .getOrElse {
                    Log.e(TAG, "Invalid theme in preferences: $themeName, defaulting to SYSTEM")
                    Theme.SYSTEM
                }
        }


    suspend fun changeTheme(theme: Theme) {
        dataStore.edit { preference ->
            preference[CURRENT_THEME] = theme.name
        }
    }

}