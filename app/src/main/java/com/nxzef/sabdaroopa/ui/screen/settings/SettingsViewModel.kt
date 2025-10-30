package com.nxzef.sabdaroopa.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxzef.sabdaroopa.data.model.LabelLanguage
import com.nxzef.sabdaroopa.data.model.TableFontSize
import com.nxzef.sabdaroopa.data.model.Theme
import com.nxzef.sabdaroopa.data.repository.UserPreferencesRepository
import com.nxzef.sabdaroopa.ui.screen.quiz.Mode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            preferencesRepository.userPreferencesFlow.collect { preferences ->
                _state.update { it.copy(preferences = preferences, isLoading = false) }
            }
        }
    }

    fun updateTableFontSize(tableFontSize: TableFontSize) {
        viewModelScope.launch {
            preferencesRepository.updateTableFontSize(tableFontSize)
        }
    }

    fun updateLabelLanguage(labelLanguage: LabelLanguage) {
        viewModelScope.launch {
            preferencesRepository.updateLabelLanguage(labelLanguage)
        }
    }

    fun updateVibrationState(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateVibrationState(enabled)
        }
    }

    fun updateTheme(theme: Theme) {
        viewModelScope.launch {
            preferencesRepository.updateTheme(theme)
        }
    }

    fun updateDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateDynamicColorEnabled(enabled)
        }
    }

    fun updateDefaultMode(mode: Mode) {
        viewModelScope.launch {
            preferencesRepository.updateDefaultMode(mode)
        }
    }

    fun updateDefaultRange(range: Int) {
        viewModelScope.launch {
            preferencesRepository.updateDefaultRange(range)
        }
    }
}