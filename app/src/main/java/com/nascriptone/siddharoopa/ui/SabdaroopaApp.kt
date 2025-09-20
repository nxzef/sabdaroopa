package com.nascriptone.siddharoopa.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.nascriptone.siddharoopa.data.model.Category
import com.nascriptone.siddharoopa.data.model.Sound
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
import com.nascriptone.siddharoopa.ui.screen.quiz.QuizResultScreenTopBar
import com.nascriptone.siddharoopa.ui.screen.quiz.QuizReviewScreenTopBar
import com.nascriptone.siddharoopa.ui.screen.quiz.QuizTopBar
import com.nascriptone.siddharoopa.ui.screen.quiz.QuizViewModel
import com.nascriptone.siddharoopa.ui.screen.search.SearchScreen
import com.nascriptone.siddharoopa.ui.screen.search.SearchScreenBar
import com.nascriptone.siddharoopa.ui.screen.settings.SettingsTopBar
import com.nascriptone.siddharoopa.ui.screen.table.TableScreen
import com.nascriptone.siddharoopa.ui.screen.table.TableScreenTopBar
import com.nascriptone.siddharoopa.ui.theme.SabdaroopaTheme
import com.nascriptone.siddharoopa.utils.extensions.sharedViewModelOrNull
import com.nascriptone.siddharoopa.utils.extensions.toPascalCase
import com.nascriptone.siddharoopa.utils.isDarkTheme
import com.nascriptone.siddharoopa.viewmodel.SiddharoopaViewModel
import kotlinx.coroutines.launch

@Composable
fun SabdaroopaApp(
    mainViewModel: MainViewModel = hiltViewModel(),
    viewModel: SiddharoopaViewModel = hiltViewModel()
) {
    val settingsUIState by viewModel.appPreferencesState.collectAsStateWithLifecycle()
    val userPrefTheme = settingsUIState.currentTheme
    val systemTheme = isSystemInDarkTheme()
    val targetState = remember(userPrefTheme, systemTheme) {
        isDarkTheme(userPrefTheme, systemTheme)
    }
    AnimatedContent(
        targetState, transitionSpec = { fadeIn() togetherWith fadeOut() }) { darkTheme ->
        SabdaroopaTheme(darkTheme = darkTheme) {
            DrawerNavigation(
                mainViewModel = mainViewModel
            )
        }
    }
}

@Composable
fun DrawerNavigation(
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val isInFocused by mainViewModel.controllerUseCase.isInFocused.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val config = LocalConfiguration.current
    val orientation = config.orientation
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentNavigation by remember(backStackEntry) {
        derivedStateOf {
            backStackEntry?.destination?.route.getNavigationOrDefault(Navigation.Home)
        }
    }
    val enabledRoutes = remember {
        setOf(
            Routes.Main.withRoot,
            Routes.FavoritesHome.withRoot
        )
    }
    Surface {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = backStackEntry?.destination?.route in enabledRoutes && !isInFocused,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier
                        .fillMaxWidth(
                            fraction = if (orientation == Configuration.ORIENTATION_LANDSCAPE) 0.4f
                            else 0.8f
                        )
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(16.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider()
                    Column(Modifier.padding(horizontal = 8.dp)) {
                        Spacer(Modifier.height(16.dp))
                        Navigation.entries.forEach { navigation ->
                            NavigationDrawerItem(label = {
                                Text(navigation.name)
                            }, icon = {
                                Icon(
                                    imageVector = navigation.icon,
                                    contentDescription = navigation.name
                                )
                            }, selected = navigation == currentNavigation, onClick = {
                                scope.launch {
                                    drawerState.close()
                                }.invokeOnCompletion {
                                    navController.navigate(navigation.name) {
                                        popUpTo(Routes.Main.withRoot) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                }
                            })
                        }
                    }
                }
            },
            modifier = modifier
        ) {
            AppScaffold(
                onMenuClick = { scope.launch { drawerState.open() } }, navController = navController
            )
        }
    }
}

@Composable
fun AppScaffold(
    onMenuClick: () -> Unit, navController: NavHostController, modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        topBar = {
            AppTopBar(
                onMenuClick = onMenuClick,
                onBackPress = { navController.navigateUp() },
                navController = navController
            )
        }, snackbarHost = { SnackbarHost(snackbarHostState) }, modifier = modifier
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Navigation.Home.name,
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            navigation(
                route = Navigation.Home.name,
                startDestination = Routes.Main.withRoot
            ) {
                composable(route = Routes.Main.withRoot) {
                    HomeScreen(
                        onCardClick = { category, sound ->
                            val route = "${Routes.SabdaList.withRoot}/$category/$sound"
                            navController.navigate(route)
                        }
                    )
                }
                composable(
                    route = "${Routes.SabdaList.withRoot}/{c}/{s}",
                    arguments = listOf(navArgument("c") {
                        type = NavType.IntType
                    }, navArgument("s") {
                        type = NavType.IntType
                    })
                ) { backStackEntry ->

                    val categoryIndex = backStackEntry.arguments?.getInt("c") ?: return@composable
                    val soundIndex = backStackEntry.arguments?.getInt("s") ?: return@composable
                    val initialCategory =
                        Category.entries.getOrElse(categoryIndex) { Category.GENERAL }
                    val initialSound = Sound.entries.getOrElse(soundIndex) { Sound.VOWELS }

                    CategoryScreen(
                        category = initialCategory,
                        initialSound = initialSound,
                        onSabdaClick = { id ->
                            val route = "${Navigation.Home.name}/${Routes.Table.name}/$id"
                            navController.navigate(route) {
                                launchSingleTop = true
                            }
                        },
                    )
                }
                composable(
                    route = "${Routes.Table.withRoot}/{id}", arguments = listOf(navArgument("id") {
                        type = NavType.IntType
                    })
                ) {
                    TableScreen(
                        onQuizClick = { id -> }, snackbarHostState = snackbarHostState
                    )
                }
                composable(
                    route = Routes.Search.withRoot,
                    enterTransition = { fadeIn() + scaleIn(initialScale = 0.8f) },
                    exitTransition = { fadeOut() + scaleOut(targetScale = 1.2f) }) {
                    SearchScreen(
                        emptyList()
                    )
                }
            }
            navigation(
                route = Navigation.Favorites.name, startDestination = Routes.FavoritesHome.withRoot
            ) {
                composable(route = Routes.FavoritesHome.withRoot) { backStackEntry ->
                    FavoritesScreen(
                        onTableClick = { id ->
                            val route = "${Routes.Table.withRoot}/$id"
                            navController.navigate(route)
                        },
                        favoritesViewModel = navController.sharedViewModelOrNull(Navigation.Favorites.name)
                    )
                }
            }
            navigation(
                route = Navigation.Quiz.name, startDestination = Routes.QuizHome.withRoot
            ) {
                composable(route = Routes.QuizHome.withRoot) {
                    val quizViewModel: QuizViewModel = hiltViewModel()
                    QuizHomeScreen(
                        onBeginQuiz = {},
                        onFromListClick = {},
                        onFromFavoritesClick = {
                            navController.navigate(Navigation.Favorites.name) {
                                popUpTo(Routes.QuizHome.withRoot) {
                                    inclusive = false
                                    saveState = true
                                }
                            }
                        },
                        quizViewModel = quizViewModel
                    )
                }
                composable(route = Routes.QuizQuestion.name) {
//                    val quizViewModel: QuizViewModel = hiltViewModel()
//                    QuizQuestionScreen(
//                        viewModel = viewModel,
//                        quizSectionState = quizUIState,
//                        navHostController = navHostController
//                    )
                }
                composable(route = Routes.QuizResult.withRoot) {
//                    val quizViewModel: QuizViewModel = hiltViewModel()
//                    QuizResultScreen(
//                        viewModel = viewModel,
//                        quizSectionState = quizUIState,
//                        navHostController = navHostController
//                    )
                }
                composable(route = Routes.QuizReview.withRoot) {
//                    val quizViewModel: QuizViewModel = hiltViewModel()
//                    QuizReviewScreen(quizSectionState = quizUIState)
                }
                composable(route = Routes.QuizInstruction.withRoot) {
                    QuizInstructionScreen(onBackPress = { navController.navigateUp() })
                }
            }
            navigation(
                route = Navigation.Settings.name, startDestination = Routes.SettingsHome.withRoot
            ) {
                composable(route = Routes.SettingsHome.withRoot) {
//                    SettingsScreen(
//                        settingsUIState = settingsUIState,
//                        viewModel = viewModel
//                    )
                }
            }
        }
    }
}

@Composable
fun AppTopBar(
    onMenuClick: () -> Unit,
    onBackPress: () -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute by remember(backStackEntry) {
        derivedStateOf {
            backStackEntry?.destination?.route.getRouteOrDefault(Routes.Main)
        }
    }
    AnimatedContent(
        targetState = currentRoute, transitionSpec = {
            fadeIn() + scaleIn(initialScale = 0.8f) togetherWith fadeOut() + scaleOut(targetScale = 1.2f)
        }, label = "TopBarAnimation", modifier = modifier
    ) { route ->
        when (route) {
            Routes.Main -> HomeTopBar(
                onMenuClick = onMenuClick,
                onSearchClick = { navController.navigate(Routes.Search.withRoot) }
            )

            Routes.Search -> SearchScreenBar(onBackPress)
            Routes.SabdaList -> {
                val index = backStackEntry?.arguments?.getInt("c") ?: return@AnimatedContent
                val title = Category.entries[index].toPascalCase()
                CategoryScreenTopBar(
                    title = title,
                    onBackPress = onBackPress,
                    onSearchClick = { navController.navigate(Routes.Search.withRoot) }
                )
            }

            Routes.Table -> TableScreenTopBar(onBackPress)
            Routes.FavoritesHome -> FavoritesTopBar(navHostController = navController)

            Routes.SettingsHome -> SettingsTopBar(onBackPress)
            Routes.QuizHome -> QuizTopBar(
                onBackPress = onBackPress,
                onInfoActionClick = { navController.navigate(Routes.QuizInstruction.withRoot) }
            )

            Routes.QuizResult -> QuizResultScreenTopBar()
            Routes.QuizReview -> QuizReviewScreenTopBar(onBackPress)
            Routes.QuizInstruction -> QuizInstructionScreenTopBar(onBackPress)
            else -> {}
        }
    }
}

private fun String?.getRouteOrDefault(default: Routes): Routes = this?.let {
    val value = it.substringAfter("/").substringBefore("/").split("?")[0]
    Routes.entries.firstOrNull { route -> route.name == value } ?: default
} ?: default

private fun String?.getNavigationOrDefault(default: Navigation): Navigation = this?.let {
    val value = it.substringBefore("/")
    Navigation.entries.firstOrNull { route -> route.name == value } ?: default
} ?: default

