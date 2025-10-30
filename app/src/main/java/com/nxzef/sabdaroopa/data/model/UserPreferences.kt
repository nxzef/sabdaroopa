package com.nxzef.sabdaroopa.data.model

import com.nxzef.sabdaroopa.domain.platform.SystemCapabilities
import com.nxzef.sabdaroopa.ui.screen.quiz.Mode
import kotlinx.serialization.Serializable

@Serializable
data class UserPreferences(
    val tableFontSize: TableFontSize = TableFontSize.Medium,
    val labelLanguage: LabelLanguage = LabelLanguage.Sanskrit,
    val isVibrationEnabled: Boolean = true,
    val theme: Theme = Theme.defaultTheme(),
    val dynamicColorEnabled: Boolean = true,
    val defaultMode: Mode = Mode.All,
    val defaultRange: Int = 10
)

@Serializable
enum class Theme {
    LIGHT,
    DARK,
    SYSTEM;


    val isAvailable: Boolean
        get() = when (this) {
            SYSTEM -> SystemCapabilities.supportsSystemTheme
            else -> true
        }

    companion object {

        fun getAvailableThemes(): List<Theme> {
            return entries.filter { it.isAvailable }
        }


        fun defaultTheme(): Theme {
            return if (SystemCapabilities.supportsSystemTheme) SYSTEM else LIGHT
        }
    }
}

enum class LabelLanguage {
    English, Sanskrit
}

enum class TableFontSize {
    Small, Medium, Large
}