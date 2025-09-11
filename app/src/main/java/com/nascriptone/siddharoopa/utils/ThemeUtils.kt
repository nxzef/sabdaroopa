package com.nascriptone.siddharoopa.core.utils

import com.nascriptone.siddharoopa.ui.screen.settings.Theme

fun isDarkTheme(theme: Theme, systemTheme: Boolean): Boolean {
    return when (theme) {
        Theme.SYSTEM -> systemTheme
        Theme.LIGHT -> false
        Theme.DARK -> true
    }
}