package com.nascriptone.siddharoopa.ui

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.data.model.UserPreferences
import com.nascriptone.siddharoopa.di.AppEntryPoint
import com.nascriptone.siddharoopa.ui.screen.Navigation
import com.nascriptone.siddharoopa.ui.screen.Routes
import com.nascriptone.siddharoopa.ui.screen.favorites.FavoritesScreen
import com.nascriptone.siddharoopa.ui.screen.favorites.FavoritesTopBar
import com.nascriptone.siddharoopa.ui.screen.favorites.FavoritesViewModel
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
import com.nascriptone.siddharoopa.ui.screen.quiz.QuizViewModel
import com.nascriptone.siddharoopa.ui.screen.settings.SettingsScreen
import com.nascriptone.siddharoopa.ui.screen.settings.SettingsTopBar
import com.nascriptone.siddharoopa.ui.screen.table.TableScreen
import com.nascriptone.siddharoopa.ui.screen.table.TableScreenTopBar
import com.nascriptone.siddharoopa.ui.theme.SabdaroopaTheme
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.launch

/**
 * Top-level composable for Sabdaroopa app.
 *
 * This composable manages app-wide state (theme, focus) and provides
 * the navigation structure without resetting state on theme changes.
 */
@Composable
fun SabdaroopaApp() {
    val context = LocalContext.current

    // --- Hilt Entry Points ---
    val appEntryPoint = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            AppEntryPoint::class.java
        )
    }
    val focusManager = remember { appEntryPoint.focusManager() }
    val userPreferencesRepository = remember { appEntryPoint.userPreferencesRepository() }

    // --- App-wide State ---
    val isFocused by focusManager.isFocused.collectAsStateWithLifecycle()
    val userPreferences by userPreferencesRepository.userPreferencesFlow.collectAsStateWithLifecycle(
        initialValue = UserPreferences()
    )

    // --- Navigation & Drawer State ---
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val backStackEntry by navController.currentBackStackEntryAsState()

    // Compute current navigation when backstack changes
    val currentNavigation by remember(backStackEntry) {
        derivedStateOf { backStackEntry?.destination?.route.getNavigationOrDefault() }
    }

    // Drawer gestures only for allowed routes
    val drawerEnabledRoutes = remember {
        setOf(Routes.Main.withRoot, Routes.FavoritesHome.withRoot)
    }
    val isDrawerGestureEnabled = remember(backStackEntry, isFocused) {
        backStackEntry?.destination?.route in drawerEnabledRoutes && !isFocused
    }

    // --- Back Handling ---
    if (drawerState.isOpen) {
        BackHandler { scope.launch { drawerState.close() } }
    }

    // --- Theming & Content ---
    SabdaroopaTheme(
        userTheme = userPreferences.theme,
        dynamicColor = userPreferences.dynamicColorEnabled
    ) {
        Surface {
            ModalNavigationDrawer(
                drawerState = drawerState,
                gesturesEnabled = isDrawerGestureEnabled,
                drawerContent = {
                    DrawerContent(
                        currentNavigation = currentNavigation,
                        onNavigationClick = { navigation ->
                            scope.launch { drawerState.close() }.invokeOnCompletion {
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

/**
 * Drawer content composable.
 *
 * Separated for better composition and reusability.
 */
@Composable
private fun DrawerContent(
    currentNavigation: Navigation,
    onNavigationClick: (Navigation) -> Unit,
    modifier: Modifier = Modifier
) {
    val config = LocalConfiguration.current
    val isLandscape = config.orientation == Configuration.ORIENTATION_LANDSCAPE

    ModalDrawerSheet(
        modifier = modifier
            .fillMaxWidth(fraction = if (isLandscape) 0.4f else 0.8f)
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(16.dp)
        )

        Spacer(Modifier.height(16.dp))
        HorizontalDivider()

        // Navigation items
        Column(Modifier.padding(horizontal = 8.dp)) {
            Spacer(Modifier.height(16.dp))

            Navigation.entries.forEach { navigation ->
                NavigationDrawerItem(
                    label = { Text(navigation.name) },
                    icon = {
                        Icon(
                            imageVector = navigation.icon, contentDescription = navigation.name
                        )
                    },
                    selected = navigation == currentNavigation,
                    onClick = { onNavigationClick(navigation) })
            }
        }
    }
}

/**
 * Main scaffold with top bar and navigation host.
 *
 * Separated to isolate scaffold-specific logic.
 */
@Composable
private fun AppScaffold(
    onMenuClick: () -> Unit, navController: NavHostController, modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            AppTopBar(
                onMenuClick = onMenuClick,
                navController = navController
            )
        }, snackbarHost = { SnackbarHost(snackbarHostState) }, modifier = modifier
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

/**
 * Navigation host with all app routes.
 *
 * Separated to reduce complexity in AppScaffold.
 */
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
            route = Navigation.Home.name, startDestination = Routes.Main.withRoot
        ) {
            composable(route = Routes.Main.withRoot) {
                HomeScreen(
                    onItemClick = { id, sm ->
                        navController.navigate("${Routes.Table.withRoot}/$id?sm=$sm") {
                            launchSingleTop = true
                        }
                    },
                    navigateToQuiz = {
                        navController.navigate(Navigation.Quiz.name) {
                            launchSingleTop = true
                        }
                    },
                    navigateUp = { navController.navigateUp() },
                    snackbarHostState = snackbarHostState
                )
            }

            composable(
                route = "${Routes.Table.withRoot}/{id}?sm={sm}",
                arguments = listOf(navArgument("id") { type = NavType.IntType }, navArgument("sm") {
                    type = NavType.BoolType
                    defaultValue = false
                })
            ) {
                TableScreen(snackbarHostState = snackbarHostState)
            }
        }

        // Favorites navigation graph
        navigation(
            route = Navigation.Favorites.name, startDestination = Routes.FavoritesHome.withRoot
        ) {
            composable(route = Routes.FavoritesHome.withRoot) { backStackEntry ->
                val viewModelStoreOwner = remember(backStackEntry) {
                    navController.getBackStackEntry(Navigation.Favorites.name)
                }
                val viewModel: FavoritesViewModel = hiltViewModel(viewModelStoreOwner)

                FavoritesScreen(
                    onTableClick = { id, sm ->
                        navController.navigate("${Routes.Table.withRoot}/$id?sm=$sm")
                    }, snackbarHostState = snackbarHostState, favoritesViewModel = viewModel
                )
            }
        }

        // Quiz navigation graph
        navigation(
            route = Navigation.Quiz.name, startDestination = Routes.QuizHome.withRoot
        ) {
            composable(route = Routes.QuizHome.withRoot) { backStackEntry ->
                val viewModelStoreOwner = remember(backStackEntry) {
                    navController.getBackStackEntry(Navigation.Quiz.name)
                }
                val viewModel: QuizViewModel = hiltViewModel(viewModelStoreOwner)

                QuizHomeScreen(
                    quizViewModel = viewModel, navHostController = navController
                )
            }

            composable(route = Routes.QuizQuestion.withRoot) { backStackEntry ->
                val viewModelStoreOwner = remember(backStackEntry) {
                    navController.getBackStackEntry(Navigation.Quiz.name)
                }
                val viewModel: QuizViewModel = hiltViewModel(viewModelStoreOwner)

                QuizQuestionScreen(
                    quizViewModel = viewModel, navHostController = navController
                )
            }

            composable(route = Routes.QuizResult.withRoot) { backStackEntry ->
                val viewModelStoreOwner = remember(backStackEntry) {
                    navController.getBackStackEntry(Navigation.Quiz.name)
                }
                val viewModel: QuizViewModel = hiltViewModel(viewModelStoreOwner)

                QuizResultScreen(
                    quizViewModel = viewModel, navHostController = navController
                )
            }

            composable(route = Routes.QuizReview.withRoot) { backStackEntry ->
                val viewModelStoreOwner = remember(backStackEntry) {
                    navController.getBackStackEntry(Navigation.Quiz.name)
                }
                val viewModel: QuizViewModel = hiltViewModel(viewModelStoreOwner)
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                QuizReviewScreen(uiState = uiState)
            }

            composable(route = Routes.QuizInstruction.withRoot) {
                QuizInstructionScreen(
                    onBackPress = { navController.navigateUp() })
            }
        }

        // Settings navigation graph
        navigation(
            route = Navigation.Settings.name, startDestination = Routes.SettingsHome.withRoot
        ) {
            composable(route = Routes.SettingsHome.withRoot) {
                SettingsScreen()
            }
        }
    }
}

/**
 * Animated top bar that changes based on current route.
 *
 * Uses AnimatedContent for smooth transitions between different top bars.
 */
@Composable
private fun AppTopBar(
    onMenuClick: () -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute by remember(backStackEntry) {
        derivedStateOf { backStackEntry?.destination?.route.getRouteOrDefault() }
    }
    val onBackPress: () -> Unit = remember(navController) {
        {
            navController.navigateUp()
        }
    }

    AnimatedContent(
        targetState = currentRoute, transitionSpec = {
            (fadeIn() + scaleIn(initialScale = 0.8f)) togetherWith (fadeOut() + scaleOut(targetScale = 1.2f))
        }, label = "TopBarAnimation", modifier = modifier
    ) { route ->
        when (route) {
            Routes.Main -> HomeTopBar(onMenuClick = onMenuClick, navHostController = navController)
            Routes.Table -> {
                val fromSelectionMode = backStackEntry?.arguments?.getBoolean("sm") ?: false
                TableScreenTopBar(
                    fromSelectionMode = fromSelectionMode, navHostController = navController
                )
            }

            Routes.FavoritesHome -> FavoritesTopBar(navHostController = navController)
            Routes.SettingsHome -> SettingsTopBar(onBackPress)
            Routes.QuizHome -> QuizTopBar(navHostController = navController)
            Routes.QuizResult -> QuizResultScreenTopBar()
            Routes.QuizReview -> QuizReviewScreenTopBar(onBackPress)
            Routes.QuizInstruction -> QuizInstructionScreenTopBar(onBackPress)
            else -> {} // No top bar for other routes
        }
    }
}

/**
 * Extracts route from navigation destination path.
 *
 * Example: "Home/main/123?param=value" -> Routes.Main
 */
private fun String?.getRouteOrDefault(): Routes {
    val default = Routes.Main
    return this?.let {
        val routeName = it.substringAfter("/").substringBefore("/").split("?")[0]
        Routes.entries.firstOrNull { route -> route.name == routeName } ?: default
    } ?: default
}

/**
 * Extracts navigation section from destination path.
 *
 * Example: "Home/main" -> Navigation.Home
 */
private fun String?.getNavigationOrDefault(): Navigation {
    val default = Navigation.Home
    return this?.let {
        val navigationName = it.substringBefore("/")
        Navigation.entries.firstOrNull { nav -> nav.name == navigationName } ?: default
    } ?: default
}