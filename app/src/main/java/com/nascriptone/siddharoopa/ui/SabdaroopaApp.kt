package com.nascriptone.siddharoopa.ui

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import com.nascriptone.siddharoopa.data.model.Table
import com.nascriptone.siddharoopa.ui.screen.Navigation
import com.nascriptone.siddharoopa.ui.screen.Routes
import com.nascriptone.siddharoopa.ui.screen.category.CategoryScreen
import com.nascriptone.siddharoopa.ui.screen.category.CategoryScreenTopBar
import com.nascriptone.siddharoopa.ui.screen.favorites.FavoritesScreen
import com.nascriptone.siddharoopa.ui.screen.favorites.FavoritesTopBar
import com.nascriptone.siddharoopa.ui.screen.home.HomeScreen
import com.nascriptone.siddharoopa.ui.screen.home.HomeTopBar
import com.nascriptone.siddharoopa.ui.screen.quiz.QuizHomeScreen
import com.nascriptone.siddharoopa.ui.screen.quiz.QuizInstructionScreen
import com.nascriptone.siddharoopa.ui.screen.quiz.QuizInstructionScreenTopBar
import com.nascriptone.siddharoopa.ui.screen.quiz.QuizQuestionScreen
import com.nascriptone.siddharoopa.ui.screen.quiz.QuizResultScreen
import com.nascriptone.siddharoopa.ui.screen.quiz.QuizResultScreenTopBar
import com.nascriptone.siddharoopa.ui.screen.quiz.QuizReviewScreen
import com.nascriptone.siddharoopa.ui.screen.quiz.QuizReviewScreenTopBar
import com.nascriptone.siddharoopa.ui.screen.quiz.QuizTopBar
import com.nascriptone.siddharoopa.ui.screen.settings.SettingsScreen
import com.nascriptone.siddharoopa.ui.screen.settings.SettingsTopBar
import com.nascriptone.siddharoopa.ui.screen.table.TableScreen
import com.nascriptone.siddharoopa.ui.screen.table.TableScreenTopBar
import com.nascriptone.siddharoopa.ui.theme.SabdaroopaTheme
import com.nascriptone.siddharoopa.viewmodel.SiddharoopaViewModel

@Composable
fun SabdaroopaApp(
    modifier: Modifier = Modifier,
    viewModel: SiddharoopaViewModel = hiltViewModel(),
    navHostController: NavHostController = rememberNavController()
) {

    val entireSabdaList by viewModel.entireSabdaList.collectAsStateWithLifecycle()
    val homeUiState by viewModel.homeUIState.collectAsStateWithLifecycle()
    val categoryScreenState by viewModel.categoryUIState.collectAsStateWithLifecycle()
    val tableUIState by viewModel.tableUIState.collectAsStateWithLifecycle()
    val favoritesUIState by viewModel.favoritesUIState.collectAsStateWithLifecycle()
    val quizUIState by viewModel.quizUIState.collectAsStateWithLifecycle()
    val settingsUIState by viewModel.settingsUIState.collectAsStateWithLifecycle()

    val backStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute by remember(backStackEntry) {
        derivedStateOf {
            backStackEntry?.destination?.route?.let { Routes.valueOf(it) } ?: Routes.Home
        }
    }

    val userPrefTheme = settingsUIState.currentTheme
    val systemTheme = isSystemInDarkTheme()
    val isDark = remember(userPrefTheme, systemTheme) {
        isDarkTheme(userPrefTheme, systemTheme)
    }

    val onBackPress: () -> Unit = { navHostController.navigateUp() }
    val snackbarHostState = remember { SnackbarHostState() }


    AnimatedContent(
        isDark, transitionSpec = { fadeIn() togetherWith fadeOut() }) { darkTheme ->
        SabdaroopaTheme(
            darkTheme = darkTheme
        ) {
            Surface {
                Scaffold(
                    topBar = {
                        AppTopBar(
                            navHostController = navHostController,
                            currentRoute = currentRoute,
                            categoryScreenTitle = categoryScreenState.selectedTable,
                            tableScreenTitle = tableUIState.selectedSabda?.sabda?.word,
                            onBackPress = onBackPress
                        )
                    }, snackbarHost = {
                        SnackbarHost(snackbarHostState)
                    }, modifier = modifier
                ) {
                    NavHost(
                        navController = navHostController,
                        startDestination = Routes.Home.name,
                        modifier = modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        composable(Routes.Home.name) {
                            HomeScreen(
                                viewModel = viewModel,
                                navHostController = navHostController,
                                homeScreenState = homeUiState,
                            )
                        }
                        composable(Routes.Category.name) {
                            CategoryScreen(
                                viewModel = viewModel,
                                navHostController = navHostController,
                                entireSabdaList = entireSabdaList,
                                categoryScreenState = categoryScreenState
                            )
                        }
                        composable(Routes.Table.name) {
                            TableScreen(
                                tableUIState = tableUIState,
                                entireSabdaList = entireSabdaList,
                                viewModel = viewModel,
                                snackbarHostState = snackbarHostState
                            )
                        }
                        navigation(
                            route = Navigation.Favorites.name,
                            startDestination = Routes.FavoritesHome.name
                        ) {
                            composable(Routes.FavoritesHome.name) {
                                FavoritesScreen(
                                    viewModel = viewModel,
                                    navHostController = navHostController,
                                    favoritesUIState = favoritesUIState,
                                    entireSabdaList = entireSabdaList
                                )
                            }
                        }
                        navigation(
                            route = Navigation.Settings.name,
                            startDestination = Routes.SettingsHome.name
                        ) {
                            composable(Routes.SettingsHome.name) {
                                SettingsScreen(
                                    settingsUIState = settingsUIState, viewModel = viewModel
                                )
                            }
                        }
                        navigation(
                            route = Navigation.Quiz.name, startDestination = Routes.QuizHome.name
                        ) {
                            composable(Routes.QuizHome.name) {
                                QuizHomeScreen(
                                    viewModel = viewModel,
                                    quizSectionState = quizUIState,
                                    navHostController = navHostController
                                )
                            }
                            composable(Routes.QuizQuestion.name) {
                                QuizQuestionScreen(
                                    viewModel = viewModel,
                                    quizSectionState = quizUIState,
                                    navHostController = navHostController
                                )
                            }
                            composable(Routes.QuizInstruction.name) {
                                QuizInstructionScreen(
                                    onBackPress = onBackPress
                                )
                            }
                            composable(Routes.QuizResult.name) {
                                QuizResultScreen(
                                    viewModel = viewModel,
                                    quizSectionState = quizUIState,
                                    navHostController = navHostController
                                )
                            }
                            composable(Routes.QuizReview.name) {
                                QuizReviewScreen(
                                    quizSectionState = quizUIState
                                )
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
    currentRoute: Routes,
    categoryScreenTitle: Table?,
    tableScreenTitle: String?,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier
) {

    AnimatedContent(
        targetState = currentRoute,
        transitionSpec = {
            fadeIn() + scaleIn(initialScale = 0.8f) togetherWith
                    fadeOut() + scaleOut(targetScale = 1.2f)
        },
        label = "TopBarAnimation",
        modifier = modifier
    ) { route ->
        when (route) {
            Routes.Home -> HomeTopBar(navHostController)
            Routes.Category -> {
                val title = categoryScreenTitle?.let { stringResource(it.skt) }.orEmpty()
                CategoryScreenTopBar(title = title, onBackPress = onBackPress)
            }

            Routes.Table -> {
                val title = tableScreenTitle.orEmpty()
                TableScreenTopBar(title = title, onBackPress = onBackPress)
            }

            Routes.FavoritesHome -> FavoritesTopBar(onBackPress)
            Routes.SettingsHome -> SettingsTopBar(onBackPress)
            Routes.QuizHome -> {
                QuizTopBar(
                    onBackPress = onBackPress,
                    onInfoActionClick = {
                        navHostController.navigate(Routes.QuizInstruction.name)
                    }
                )
            }

            Routes.QuizResult -> QuizResultScreenTopBar()
            Routes.QuizReview -> QuizReviewScreenTopBar(onBackPress)
            Routes.QuizInstruction -> QuizInstructionScreenTopBar(onBackPress)
            else -> {}
        }
    }
}
