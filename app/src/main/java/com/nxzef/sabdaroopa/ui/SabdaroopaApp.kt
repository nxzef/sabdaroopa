package com.nxzef.sabdaroopa.ui

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.nxzef.sabdaroopa.R
import com.nxzef.sabdaroopa.data.model.UserPreferences
import com.nxzef.sabdaroopa.di.AppEntryPoint
import com.nxzef.sabdaroopa.ui.screen.Navigation
import com.nxzef.sabdaroopa.ui.screen.Routes
import com.nxzef.sabdaroopa.ui.screen.about.AboutScreen
import com.nxzef.sabdaroopa.ui.screen.about.AboutTopBar
import com.nxzef.sabdaroopa.ui.screen.favorites.FavoritesScreen
import com.nxzef.sabdaroopa.ui.screen.favorites.FavoritesTopBar
import com.nxzef.sabdaroopa.ui.screen.favorites.FavoritesViewModel
import com.nxzef.sabdaroopa.ui.screen.home.HomeScreen
import com.nxzef.sabdaroopa.ui.screen.home.HomeTopBar
import com.nxzef.sabdaroopa.ui.screen.quiz.QuizHomeScreen
import com.nxzef.sabdaroopa.ui.screen.quiz.QuizInstructionScreen
import com.nxzef.sabdaroopa.ui.screen.quiz.QuizInstructionScreenTopBar
import com.nxzef.sabdaroopa.ui.screen.quiz.QuizQuestionScreen
import com.nxzef.sabdaroopa.ui.screen.quiz.QuizResultScreen
import com.nxzef.sabdaroopa.ui.screen.quiz.QuizResultScreenTopBar
import com.nxzef.sabdaroopa.ui.screen.quiz.QuizReviewScreen
import com.nxzef.sabdaroopa.ui.screen.quiz.QuizReviewScreenTopBar
import com.nxzef.sabdaroopa.ui.screen.quiz.QuizTopBar
import com.nxzef.sabdaroopa.ui.screen.quiz.QuizViewModel
import com.nxzef.sabdaroopa.ui.screen.settings.SettingsScreen
import com.nxzef.sabdaroopa.ui.screen.settings.SettingsTopBar
import com.nxzef.sabdaroopa.ui.screen.table.TableScreen
import com.nxzef.sabdaroopa.ui.screen.table.TableScreenTopBar
import com.nxzef.sabdaroopa.ui.theme.SabdaroopaTheme
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.launch

@Composable
fun SabdaroopaApp() {
    val context = LocalContext.current

    val (userPreferencesRepository, focusManager, hapticManager) = remember {
        val appEntryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            AppEntryPoint::class.java
        )
        Triple(
            appEntryPoint.userPreferencesRepository(),
            appEntryPoint.focusManager(),
            appEntryPoint.hapticManager()
        )
    }

    val userPreferences by userPreferencesRepository.userPreferencesFlow
        .collectAsStateWithLifecycle(initialValue = UserPreferences())


    SabdaroopaTheme(
        userTheme = userPreferences.theme,
        dynamicColor = userPreferences.dynamicColorEnabled
    ) {
        CompositionLocalProvider(
            LocalUserPreferences provides userPreferences,
            LocalHapticManager provides hapticManager
        ) {

            val navController = rememberNavController()
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            val backStackEntry by navController.currentBackStackEntryAsState()

            val currentNavigation by remember(backStackEntry) {
                derivedStateOf {
                    backStackEntry?.destination?.route.getNavigationOrDefault()
                }
            }

            val isFocused by focusManager.isFocused.collectAsStateWithLifecycle()

            val drawerEnabledRoutes = remember {
                setOf(Routes.Main.withRoot, Routes.FavoritesHome.withRoot)
            }

            val isDrawerGestureEnabled by remember(backStackEntry, isFocused) {
                derivedStateOf {
                    backStackEntry?.destination?.route in drawerEnabledRoutes && !isFocused
                }
            }

            if (drawerState.isOpen) BackHandler { scope.launch { drawerState.close() } }

            Surface {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    gesturesEnabled = isDrawerGestureEnabled,
                    drawerContent = {
                        DrawerContent(
                            currentNavigation = currentNavigation,
                            onNavigationClick = { navigation ->
                                scope.launch {
                                    drawerState.close()
                                    navController.navigate(navigation.name) {
                                        popUpTo(Routes.Main.withRoot) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                ) {
                    AppScaffold(
                        onMenuClick = { scope.launch { drawerState.open() } },
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
private fun DrawerContent(
    currentNavigation: Navigation,
    onNavigationClick: (Navigation) -> Unit,
    modifier: Modifier = Modifier
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    ModalDrawerSheet(
        modifier = modifier
            .fillMaxWidth(fraction = if (isLandscape) 0.4f else 0.8f)
            .fillMaxHeight()
    ) {
        LazyColumn {
            item {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(16.dp)
                )
            }

            item { Spacer(Modifier.height(16.dp)) }
            item { HorizontalDivider() }
            item { Spacer(Modifier.height(16.dp)) }

            items(
                items = Navigation.entries,
                key = { it.name }
            ) { navigation ->
                NavigationDrawerItem(
                    label = { Text(navigation.name) },
                    icon = {
                        Icon(
                            imageVector = navigation.icon,
                            contentDescription = navigation.name
                        )
                    },
                    selected = navigation == currentNavigation,
                    onClick = { onNavigationClick(navigation) },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun AppScaffold(
    onMenuClick: () -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            AppTopBar(
                onMenuClick = onMenuClick,
                navController = navController
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        AppNavHost(
            navController = navController,
            snackbarHostState = snackbarHostState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Navigation.Home.name,
        modifier = modifier
    ) {
        // Home navigation graph
        navigation(
            route = Navigation.Home.name,
            startDestination = Routes.Main.withRoot
        ) {
            composable(route = Routes.Main.withRoot) {
                HomeScreen(
                    onItemClick = { id, sm ->
                        val route = "${Routes.Table.withRoot}/$id?sm=$sm"
                        navController.navigate(route) {
                            launchSingleTop = true
                        }
                    },
                    navigateToQuiz = {
                        navController.navigate(Navigation.Quiz.name) {
                            launchSingleTop = true
                        }
                    },
                    navigateUp = navController::navigateUp,
                    snackbarHostState = snackbarHostState
                )
            }

            composable(
                route = "${Routes.Table.withRoot}/{id}?sm={sm}",
                arguments = listOf(
                    navArgument("id") { type = NavType.IntType },
                    navArgument("sm") {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) {
                TableScreen(snackbarHostState = snackbarHostState)
            }
        }

        // Favorites navigation graph
        navigation(
            route = Navigation.Favorites.name,
            startDestination = Routes.FavoritesHome.withRoot
        ) {
            composable(route = Routes.FavoritesHome.withRoot) { backStackEntry ->
                val viewModelStoreOwner = remember(backStackEntry) {
                    navController.getBackStackEntry(Navigation.Favorites.name)
                }
                val favoritesViewModel: FavoritesViewModel = hiltViewModel(viewModelStoreOwner)

                FavoritesScreen(
                    onTableClick = { id, sm ->
                        val route = "${Routes.Table.withRoot}/$id?sm=$sm"
                        navController.navigate(route) {
                            launchSingleTop = true
                        }
                    },
                    snackbarHostState = snackbarHostState,
                    favoritesViewModel = favoritesViewModel
                )
            }
        }

        // Quiz navigation graph
        navigation(
            route = Navigation.Quiz.name,
            startDestination = Routes.QuizHome.withRoot
        ) {
            composable(route = Routes.QuizHome.withRoot) { backStackEntry ->
                val viewModelStoreOwner = remember(backStackEntry) {
                    navController.getBackStackEntry(Navigation.Quiz.name)
                }
                val quizViewModel: QuizViewModel = hiltViewModel(viewModelStoreOwner)

                QuizHomeScreen(
                    quizViewModel = quizViewModel,
                    navHostController = navController
                )
            }

            composable(route = Routes.QuizQuestion.withRoot) { backStackEntry ->
                val viewModelStoreOwner = remember(backStackEntry) {
                    navController.getBackStackEntry(Navigation.Quiz.name)
                }
                val quizViewModel: QuizViewModel = hiltViewModel(viewModelStoreOwner)

                QuizQuestionScreen(
                    quizViewModel = quizViewModel,
                    navHostController = navController
                )
            }

            composable(route = Routes.QuizResult.withRoot) { backStackEntry ->
                val viewModelStoreOwner = remember(backStackEntry) {
                    navController.getBackStackEntry(Navigation.Quiz.name)
                }
                val quizViewModel: QuizViewModel = hiltViewModel(viewModelStoreOwner)

                QuizResultScreen(
                    quizViewModel = quizViewModel,
                    navHostController = navController
                )
            }

            composable(route = Routes.QuizReview.withRoot) { backStackEntry ->
                val viewModelStoreOwner = remember(backStackEntry) {
                    navController.getBackStackEntry(Navigation.Quiz.name)
                }
                val quizViewModel: QuizViewModel = hiltViewModel(viewModelStoreOwner)
                val uiState by quizViewModel.uiState.collectAsStateWithLifecycle()

                QuizReviewScreen(uiState = uiState)
            }

            composable(route = Routes.QuizInstruction.withRoot) {
                QuizInstructionScreen(
                    onBackPress = navController::navigateUp
                )
            }
        }

        // Settings navigation graph
        navigation(
            route = Navigation.Settings.name,
            startDestination = Routes.SettingsHome.withRoot
        ) {
            composable(route = Routes.SettingsHome.withRoot) {
                SettingsScreen(
                    onAboutClick = {
                        navController.navigate(Routes.About.withRoot)
                    }
                )
            }
            composable(route = Routes.About.withRoot) {
                AboutScreen()
            }
        }
    }
}

@Composable
private fun AppTopBar(
    onMenuClick: () -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute by remember(backStackEntry) {
        derivedStateOf {
            backStackEntry?.destination?.route.getRouteOrDefault()
        }
    }

    AnimatedContent(
        targetState = currentRoute,
        transitionSpec = {
            (fadeIn() + scaleIn(initialScale = 0.8f)) togetherWith
                    (fadeOut() + scaleOut(targetScale = 1.2f))
        },
        label = "TopBarAnimation",
        modifier = modifier
    ) { route ->
        when (route) {
            Routes.Main -> HomeTopBar(onMenuClick = onMenuClick, navHostController = navController)
            Routes.Table -> {
                val fromSelectionMode = backStackEntry?.arguments?.getBoolean("sm") ?: false
                TableScreenTopBar(
                    fromSelectionMode = fromSelectionMode,
                    navHostController = navController
                )
            }

            Routes.FavoritesHome -> FavoritesTopBar(navHostController = navController)
            Routes.SettingsHome -> SettingsTopBar(navController::navigateUp)
            Routes.About -> AboutTopBar(navController::navigateUp)
            Routes.QuizHome -> QuizTopBar(navHostController = navController)
            Routes.QuizResult -> QuizResultScreenTopBar()
            Routes.QuizReview -> QuizReviewScreenTopBar(navController::navigateUp)
            Routes.QuizInstruction -> QuizInstructionScreenTopBar(navController::navigateUp)
            Routes.QuizQuestion -> {} // No top bar for other routes
        }
    }
}

@Stable
private fun String?.getRouteOrDefault(): Routes {
    val default = Routes.Main
    return this?.let {
        val routeName = it.substringAfter("/").substringBefore("/").split("?")[0]
        Routes.entries.firstOrNull { route -> route.name == routeName } ?: default
    } ?: default
}

@Stable
private fun String?.getNavigationOrDefault(): Navigation {
    val default = Navigation.Home
    return this?.let {
        val navigationName = it.substringBefore("/")
        Navigation.entries.firstOrNull { nav -> nav.name == navigationName } ?: default
    } ?: default
}