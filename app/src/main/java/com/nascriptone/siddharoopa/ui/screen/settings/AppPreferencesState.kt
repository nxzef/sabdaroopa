package com.nascriptone.siddharoopa.ui.screen.settings

import androidx.annotation.StringRes
import com.nascriptone.siddharoopa.R

data class AppPreferencesState(
    val currentTheme: Theme = Theme.SYSTEM
)

enum class Theme(@param:StringRes val uiName: Int) {
    SYSTEM(R.string.system_default),
    LIGHT(R.string.light),
    DARK(R.string.dark)
}