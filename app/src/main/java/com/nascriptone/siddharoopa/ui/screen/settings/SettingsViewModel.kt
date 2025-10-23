package com.nascriptone.siddharoopa.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nascriptone.siddharoopa.data.model.Theme
import com.nascriptone.siddharoopa.data.repository.UserPreferencesRepository
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
}