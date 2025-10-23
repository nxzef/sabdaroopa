package com.nascriptone.siddharoopa.ui.screen.settings

import com.nascriptone.siddharoopa.data.model.UserPreferences


data class SettingsState(
    val preferences: UserPreferences = UserPreferences(),
    val isLoading: Boolean = true
)