package com.nascriptone.siddharoopa.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import com.nascriptone.siddharoopa.ui.screen.table.TableScreen
import com.nascriptone.siddharoopa.ui.screen.table.TableScreenTopBar
import com.nascriptone.siddharoopa.viewmodel.SiddharoopaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiddharoopaApp(
    modifier: Modifier = Modifier,
    viewModel: SiddharoopaViewModel = hiltViewModel(),
    navHostController: NavHostController = rememberNavController()
) {

    val categoryScreenState by viewModel.categoryUIState.collectAsStateWithLifecycle()
    val tableUIState by viewModel.tableUIState.collectAsStateWithLifecycle()


    val backStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute by remember(backStackEntry) {
        derivedStateOf {
            backStackEntry?.destination?.route
                ?.let { SiddharoopaRoutes.valueOf(it) } ?: SiddharoopaRoutes.Home
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()


    Scaffold(
        topBar = {
            AppTopBar(
                navHostController = navHostController,
                currentRoute = currentRoute,
                homeBarScrollBehavior = scrollBehavior,
                categoryScreenTitle = categoryScreenState.selectedCategory?.title,
                tableScreenTitle = tableUIState.selectedSabda?.word
            )
        },
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    navHostController: NavHostController,
    currentRoute: SiddharoopaRoutes,
    categoryScreenTitle: String?,
    tableScreenTitle: String?,
    homeBarScrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
) {


    AnimatedVisibility(
        visible = currentRoute == SiddharoopaRoutes.Home,

        ) {
        TopAppBar(
            title = { Text("Siddharoopa") },
            scrollBehavior = homeBarScrollBehavior,
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
    ) {
        CategoryScreenTopBar(
            title = categoryScreenTitle ?: "",
            onBackPress = {
                navHostController.navigateUp()
            }
        )
    }

    AnimatedVisibility(
        visible = currentRoute == SiddharoopaRoutes.Table
    ) {
        TableScreenTopBar(
            title = tableScreenTitle ?: "",
            onBackPress = {
                navHostController.navigateUp()
            }
        )
    }
}
