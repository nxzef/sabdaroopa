package com.nascriptone.siddharoopa.data.repository

import androidx.datastore.core.DataStore
import com.nascriptone.siddharoopa.data.model.Theme
import com.nascriptone.siddharoopa.data.model.UserPreferences
import com.nascriptone.siddharoopa.domain.platform.SystemCapabilities
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<UserPreferences>
) {
    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(UserPreferences())
            } else {
                throw exception
            }
        }.map { prefs ->
            prefs.copy(
                theme = if (prefs.theme.isAvailable) prefs.theme else Theme.defaultTheme(),
                dynamicColorEnabled = if (SystemCapabilities.supportsDynamicColors) prefs.dynamicColorEnabled else false
            )
        }

    suspend fun updateTheme(theme: Theme) {
        val safeTheme = if (theme.isAvailable) theme else Theme.defaultTheme()
        dataStore.updateData { currentPrefs ->
            currentPrefs.copy(theme = safeTheme)
        }
    }

    suspend fun updateDynamicColorEnabled(enabled: Boolean) {
        if (SystemCapabilities.supportsDynamicColors) {
            dataStore.updateData { currentPrefs ->
                currentPrefs.copy(dynamicColorEnabled = enabled)
            }
        }
    }
}