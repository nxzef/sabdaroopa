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

enum class Routes(val navigation: Navigation?) {
    Main(Navigation.Home),
    SabdaList(Navigation.Home),
    Table(Navigation.Home),
    Search(Navigation.Home),
    FavoritesHome(Navigation.Favorites),
    QuizHome(Navigation.Quiz),
    QuizQuestion(Navigation.Quiz),
    QuizInstruction(Navigation.Quiz),
    QuizResult(Navigation.Quiz),
    QuizReview(Navigation.Quiz),
    SettingsHome(Navigation.Settings);

    val withRoot: String
        get() = if (this.navigation != null) {
            "${this.navigation}/${this.name}"
        } else this.name
}