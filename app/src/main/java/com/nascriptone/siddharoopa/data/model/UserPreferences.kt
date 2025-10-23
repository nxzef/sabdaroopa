package com.nascriptone.siddharoopa.data.model

import com.nascriptone.siddharoopa.domain.platform.SystemCapabilities
import kotlinx.serialization.Serializable

@Serializable
data class UserPreferences(
    val theme: Theme = Theme.defaultTheme(),
    val dynamicColorEnabled: Boolean = true
)

enum class Theme {
    LIGHT,
    DARK,
    SYSTEM;

    /**
     * Check if this theme option is available on the current device
     */
    val isAvailable: Boolean
        get() = when (this) {
            SYSTEM -> SystemCapabilities.supportsSystemTheme
            else -> true
        }

    companion object {
        /**
         * Get all themes that are available on this device
         */
        fun getAvailableThemes(): List<Theme> {
            return entries.filter { it.isAvailable }
        }

        /**
         * Get the default theme based on device capabilities
         */
        fun defaultTheme(): Theme {
            return if (SystemCapabilities.supportsSystemTheme) SYSTEM else LIGHT
        }
    }
}