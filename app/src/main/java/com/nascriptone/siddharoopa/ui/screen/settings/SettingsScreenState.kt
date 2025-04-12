package com.nascriptone.siddharoopa.ui.screen.settings

import androidx.annotation.StringRes
import com.nascriptone.siddharoopa.R
import kotlinx.coroutines.flow.Flow

data class SettingsScreenState(
    val currentTheme: Theme = Theme.SYSTEM
)

enum class Theme(@StringRes val uiName: Int) {
    SYSTEM(R.string.system_default),
    LIGHT(R.string.light),
    DARK(R.string.dark)
}