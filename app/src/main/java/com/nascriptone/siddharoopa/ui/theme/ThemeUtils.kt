package com.nascriptone.siddharoopa.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.nascriptone.siddharoopa.ui.screen.settings.Theme

@Composable
fun isDarkTheme(theme: Theme): Boolean {
    return when (theme) {
        Theme.SYSTEM -> isSystemInDarkTheme()
        Theme.LIGHT -> false
        Theme.DARK -> true
    }
}