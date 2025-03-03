package com.nascriptone.siddharoopa.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nascriptone.siddharoopa.ui.screen.SiddharoopaRoutes
import com.nascriptone.siddharoopa.ui.screen.category.CategoryScreen
import com.nascriptone.siddharoopa.ui.screen.category.CategoryScreenTopBar
import com.nascriptone.siddharoopa.ui.screen.home.HomeScreen
import com.nascriptone.siddharoopa.ui.screen.table.TableScreen
import com.nascriptone.siddharoopa.viewmodel.SiddharoopaViewModel

@Composable
fun SiddharoopaApp(
    modifier: Modifier = Modifier,
    viewModel: SiddharoopaViewModel = hiltViewModel(),
    navHostController: NavHostController = rememberNavController()
) {

    val categoryScreenState by viewModel.categoryUIState.collectAsState()

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
                categoryScreenTitle = categoryScreenState.screenTitle
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
                    navHostController = navHostController
                )
            }
            composable(
                SiddharoopaRoutes.Category.name,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        initialOffset = { w -> w / 3 }
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right
                    )
                }
            ) {
                CategoryScreen(
                    viewModel = viewModel,
                    navHostController = navHostController,
                    categoryScreenState = categoryScreenState
                )
            }

            composable(SiddharoopaRoutes.Table.name) {
                TableScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    navHostController: NavHostController,
    currentRoute: SiddharoopaRoutes,
    categoryScreenTitle: String,
    modifier: Modifier = Modifier,
) {


    AnimatedVisibility(
        visible = currentRoute == SiddharoopaRoutes.Home,
        enter = slideInHorizontally(initialOffsetX = { -it / 8 }) + fadeIn(),
        exit = slideOutHorizontally(targetOffsetX = { -it / 8 }) + fadeOut()

    ) {
        TopAppBar(
            title = { Text("Siddharoopa") },
            actions = {
                IconButton(onClick = {}) {
                    Icon(Icons.Rounded.Search, null)
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Rounded.MoreVert, null)
                }
            },
            modifier = modifier
        )
    }

    AnimatedVisibility(
        visible = currentRoute == SiddharoopaRoutes.Category,
        enter = slideInHorizontally(
            animationSpec = spring()
        ) { it / 3 } + fadeIn(),
        exit = slideOutHorizontally { it / 3 } + fadeOut()
    ) {
        CategoryScreenTopBar(
            title = categoryScreenTitle,
            onBackPress = {
                navHostController.navigateUp()
            }
        )
    }
}
