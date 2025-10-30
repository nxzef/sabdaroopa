package com.nxzef.sabdaroopa.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.nxzef.sabdaroopa.data.model.UserPreferences
import com.nxzef.sabdaroopa.domain.manager.HapticManager
import com.nxzef.sabdaroopa.ui.theme.ExtendedColorScheme

val LocalUserPreferences = staticCompositionLocalOf { UserPreferences() }

val LocalHapticManager = staticCompositionLocalOf<HapticManager> {
    error("HapticManager not provided")
}

val LocalExtendedColorScheme = staticCompositionLocalOf<ExtendedColorScheme> {
    error("No ExtendedColorScheme provided")
}