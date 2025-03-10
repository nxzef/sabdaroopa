package com.nascriptone.siddharoopa.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nascriptone.siddharoopa.ui.screen.SiddharoopaRoutes
import com.nascriptone.siddharoopa.ui.screen.category.CategoryScreen
import com.nascriptone.siddharoopa.ui.screen.category.CategoryScreenTopBar
import com.nascriptone.siddharoopa.ui.screen.home.HomeScreen
import com.nascriptone.siddharoopa.ui.screen.home.HomeTopBar
import com.nascriptone.siddharoopa.ui.screen.settings.SettingsScreen
import com.nascriptone.siddharoopa.ui.screen.settings.SettingsTopBar
import com.nascriptone.siddharoopa.ui.screen.table.TableScreen
import com.nascriptone.siddharoopa.ui.screen.table.TableScreenTopBar
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

    val backStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute by remember(backStackEntry) {
        derivedStateOf {
            backStackEntry?.destination?.route
                ?.let { SiddharoopaRoutes.valueOf(it) } ?: SiddharoopaRoutes.Home
        }
    }


    Scaffold(
        topBar = {
            AppTopBar(
                navHostController = navHostController,
                currentRoute = currentRoute,
                categoryScreenTitle = categoryScreenState.selectedCategory?.title,
                tableScreenTitle = tableUIState.selectedSabda?.word
            )
        },
        modifier = modifier
    ) {
        NavHost(
            navController = navHostController,
            startDestination = SiddharoopaRoutes.Home.name,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
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
                    viewModel = viewModel
                )
            }
            composable(SiddharoopaRoutes.Settings.name) {
                SettingsScreen()
            }
        }
    }
}

@Composable
fun AppTopBar(
    navHostController: NavHostController,
    currentRoute: SiddharoopaRoutes,
    categoryScreenTitle: String?,
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

    AnimatedVisibility(
        currentRoute == SiddharoopaRoutes.Category,
    ) {
        CategoryScreenTopBar(
            title = categoryScreenTitle ?: "",
            onBackPress = onBackPress
        )
    }

    AnimatedVisibility(
        currentRoute == SiddharoopaRoutes.Table
    ) {
        TableScreenTopBar(
            title = tableScreenTitle ?: "",
            onBackPress = onBackPress
        )
    }

    AnimatedVisibility(currentRoute == SiddharoopaRoutes.Settings) {
        SettingsTopBar(
            onBackPress = onBackPress
        )
    }
}
