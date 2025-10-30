package com.nxzef.sabdaroopa.ui.screen.settings

import com.nxzef.sabdaroopa.data.model.UserPreferences


data class SettingsState(
    val preferences: UserPreferences = UserPreferences(),
    val isLoading: Boolean = true
)