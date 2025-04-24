package com.nascriptone.siddharoopa.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.nascriptone.siddharoopa.core.utils.isDarkTheme
import com.nascriptone.siddharoopa.data.model.uiobj.Table
import com.nascriptone.siddharoopa.ui.screen.SiddharoopaRoutes
import com.nascriptone.siddharoopa.ui.screen.category.CategoryScreen
import com.nascriptone.siddharoopa.ui.screen.category.CategoryScreenTopBar
import com.nascriptone.siddharoopa.ui.screen.favorites.FavoritesScreen
import com.nascriptone.siddharoopa.ui.screen.favorites.FavoritesTopBar
import com.nascriptone.siddharoopa.ui.screen.home.HomeScreen
import com.nascriptone.siddharoopa.ui.screen.home.HomeTopBar
import com.nascriptone.siddharoopa.ui.screen.quiz.QuizHomeScreen
import com.nascriptone.siddharoopa.ui.screen.quiz.QuizTopBar
import com.nascriptone.siddharoopa.ui.screen.settings.SettingsScreen
import com.nascriptone.siddharoopa.ui.screen.settings.SettingsTopBar
import com.nascriptone.siddharoopa.ui.screen.table.TableScreen
import com.nascriptone.siddharoopa.ui.screen.table.TableScreenTopBar
import com.nascriptone.siddharoopa.ui.theme.SiddharoopaTheme
import com.nascriptone.siddharoopa.viewmodel.SiddharoopaViewModel

@Composable
fun SiddharoopaApp(
    modifier: Modifier = Modifier,
    viewModel: SiddharoopaViewModel = hiltViewModel(),
    navHostController: NavHostController = rememberNavController()
) {

    val homeUiState by viewModel.homeUIState.collectAsStateWithLifecycle()
    val categoryScreenState by viewModel.categoryUIState.collectAsStateWithLifecycle()
    val tableUIState by viewModel.tableUIState.collectAsStateWithLifecycle()
    val favoritesUIState by viewModel.favoritesUIState.collectAsStateWithLifecycle()
    val settingsUIState by viewModel.settingsUIState.collectAsStateWithLifecycle()

    val backStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute by remember(backStackEntry) {
        derivedStateOf {
            backStackEntry?.destination?.route?.let { SiddharoopaRoutes.valueOf(it) }
                ?: SiddharoopaRoutes.Home
        }
    }

    val userPrefTheme = settingsUIState.currentTheme
    val systemTheme = isSystemInDarkTheme()
    val isDark = remember(userPrefTheme, systemTheme) {
        isDarkTheme(userPrefTheme, systemTheme)
    }

    val snackbarHostState = remember { SnackbarHostState() }


    AnimatedContent(
        isDark, transitionSpec = { fadeIn() togetherWith fadeOut() }) { darkTheme ->
        SiddharoopaTheme(
            darkTheme = darkTheme
        ) {
            Surface {
                Scaffold(
                    topBar = {
                        AppTopBar(
                            navHostController = navHostController,
                            currentRoute = currentRoute,
                            categoryScreenTitle = categoryScreenState.lastFetchedTable,
                            tableScreenTitle = tableUIState.currentSabda?.sabda?.word
                        )
                    },
                    snackbarHost = {
                        SnackbarHost(snackbarHostState)
                    },
                    modifier = modifier
                ) {
                    NavHost(
                        navController = navHostController,
                        startDestination = SiddharoopaRoutes.Home.name,
                        modifier = modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        composable(SiddharoopaRoutes.Home.name) {
                            HomeScreen(
                                viewModel = viewModel,
                                navHostController = navHostController,
                                homeUIState = homeUiState,
                            )
                        }
                        composable(SiddharoopaRoutes.Category.name) {
                            CategoryScreen(
                                viewModel = viewModel,
                                navHostController = navHostController,
                                categoryScreenState = categoryScreenState
                            )
                        }
                        composable(SiddharoopaRoutes.Table.name) {
                            TableScreen(
                                tableUIState = tableUIState,
                                viewModel = viewModel,
                                snackbarHostState = snackbarHostState
                            )
                        }
                        composable(SiddharoopaRoutes.Favorites.name) {
                            FavoritesScreen(
                                viewModel = viewModel,
                                navHostController = navHostController,
                                favoritesUIState = favoritesUIState
                            )
                        }
                        composable(SiddharoopaRoutes.Settings.name) {
                            SettingsScreen(
                                settingsUIState = settingsUIState, viewModel = viewModel
                            )
                        }
                        navigation(
                            route = SiddharoopaRoutes.Quiz.name,
                            startDestination = SiddharoopaRoutes.QuizHome.name
                        ) {
                            composable(SiddharoopaRoutes.QuizHome.name) {
                                QuizHomeScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppTopBar(
    navHostController: NavHostController,
    currentRoute: SiddharoopaRoutes,
    categoryScreenTitle: Table?,
    tableScreenTitle: String?,
) {

    val onBackPress: () -> Unit = {
        navHostController.navigateUp()
    }

    AnimatedVisibility(currentRoute == SiddharoopaRoutes.Home) {
        HomeTopBar(
            navHostController = navHostController
        )
    }

    AnimatedVisibility(currentRoute == SiddharoopaRoutes.Category) {
        CategoryScreenTopBar(
            title = categoryScreenTitle?.let { stringResource(it.skt) } ?: "",
            onBackPress = onBackPress
        )
    }

    AnimatedVisibility(
        currentRoute == SiddharoopaRoutes.Table
    ) {
        TableScreenTopBar(
            title = tableScreenTitle ?: "", onBackPress = onBackPress
        )
    }

    AnimatedVisibility(
        currentRoute == SiddharoopaRoutes.Favorites
    ) {
        FavoritesTopBar(
            onBackPress = onBackPress
        )
    }

    AnimatedVisibility(currentRoute == SiddharoopaRoutes.Settings) {
        SettingsTopBar(
            onBackPress = onBackPress
        )
    }

    AnimatedVisibility(currentRoute == SiddharoopaRoutes.QuizHome) {
        QuizTopBar(
            onBackPress = onBackPress
        )
    }
}
