package com.nascriptone.siddharoopa.ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class Navigation(val icon: ImageVector) {
    Home(Icons.Rounded.Home),
    Favorites(Icons.Rounded.FavoriteBorder),
    Quiz(Icons.Rounded.Quiz),
    Settings(Icons.Rounded.Settings)
}

enum class Routes {
    Main,
    SabdaList,
    Table,
    FavoritesHome,
    QuizHome,
    QuizQuestion,
    QuizInstruction,
    QuizResult,
    QuizReview,
    SettingsHome,
    Search,
}