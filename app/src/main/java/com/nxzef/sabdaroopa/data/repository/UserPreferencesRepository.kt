package com.nxzef.sabdaroopa.data.repository

import androidx.datastore.core.DataStore
import com.nxzef.sabdaroopa.data.model.LabelLanguage
import com.nxzef.sabdaroopa.data.model.TableFontSize
import com.nxzef.sabdaroopa.data.model.Theme
import com.nxzef.sabdaroopa.data.model.UserPreferences
import com.nxzef.sabdaroopa.domain.platform.SystemCapabilities
import com.nxzef.sabdaroopa.ui.screen.quiz.Mode
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

    suspend fun updateTableFontSize(tableFontSize: TableFontSize) {
        dataStore.updateData { currentPrefs ->
            currentPrefs.copy(
                tableFontSize = tableFontSize
            )
        }
    }

    suspend fun updateLabelLanguage(labelLanguage: LabelLanguage) {
        dataStore.updateData { currentPrefs ->
            currentPrefs.copy(
                labelLanguage = labelLanguage
            )
        }
    }

    suspend fun updateVibrationState(enabled: Boolean) {
        dataStore.updateData { currentPrefs ->
            currentPrefs.copy(
                isVibrationEnabled = enabled
            )
        }
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

    suspend fun updateDefaultMode(mode: Mode) {
        dataStore.updateData { currentPrefs ->
            currentPrefs.copy(
                defaultMode = mode
            )
        }
    }

    suspend fun updateDefaultRange(range: Int) {
        dataStore.updateData { currentPrefs ->
            currentPrefs.copy(
                defaultRange = range
            )
        }
    }
}