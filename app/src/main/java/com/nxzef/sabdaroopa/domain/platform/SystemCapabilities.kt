package com.nxzef.sabdaroopa.domain.platform

import android.os.Build

object SystemCapabilities {
    /**
     * Android 10 (API 29) introduced proper system theme support via isSystemInDarkTheme()
     * that respects the system-wide dark mode setting
     */
    val supportsSystemTheme: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    /**
     * Android 12 (API 31) introduced Material You dynamic colors
     */
    val supportsDynamicColors: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
}