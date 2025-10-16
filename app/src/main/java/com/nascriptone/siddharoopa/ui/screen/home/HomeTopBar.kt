package com.nascriptone.siddharoopa.ui.screen.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.ui.component.CustomToolTip
import com.nascriptone.siddharoopa.ui.component.DiscardDialog
import com.nascriptone.siddharoopa.ui.screen.Routes
import com.nascriptone.siddharoopa.ui.state.Trigger
import com.nascriptone.siddharoopa.utils.extensions.sharedViewModelOrNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    onMenuClick: () -> Unit,
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val homeViewModel: HomeViewModel? =
        navHostController.sharedViewModelOrNull(Routes.Main.withRoot)
    if (homeViewModel == null) return
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val hasAnyNonFavorite by homeViewModel.hasAnyNonFavorite.collectAsStateWithLifecycle()
    val hasSelectionChanged by homeViewModel.hasSelectionChanged.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = uiState.isSelectMode,
        transitionSpec = {
            fadeIn() + scaleIn(initialScale = 0.8f) togetherWith fadeOut() + scaleOut(targetScale = 1.2f)
        }
    ) { state ->
        if (state) SelectionBar(
            query = uiState.query,
            selectedIds = uiState.selectedIds,
            filterCount = uiState.filter.activeFilterCount,
            trigger = uiState.trigger,
            hasAnyNonFavorite = hasAnyNonFavorite,
            hasActiveFilters = uiState.filter.isActive,
            hasSelectionChanged = hasSelectionChanged,
            isSearchMode = uiState.isSearchMode,
            onQueryChange = homeViewModel::onQueryChange,
            onClearQuery = homeViewModel::onClearQuery,
            onFilterClick = homeViewModel::toggleBottomSheet,
            onFavoriteClick = homeViewModel::addSelectedItemsToFavorites,
            navigateUp = { navHostController.navigateUp() },
            toggleSelectionSearchFocus = homeViewModel::toggleSearch,
            onDiscardChanges = homeViewModel::onDiscardChanges,
            toggleSelectionMode = homeViewModel::toggleSelectionMode,
            navHostController = navHostController,
            modifier = modifier
        )
        else HomeMainBar(
            query = uiState.query,
            isSearchMode = uiState.isSearchMode,
            onQueryChange = homeViewModel::onQueryChange,
            onClearQuery = homeViewModel::onClearQuery,
            onMenuClick = onMenuClick,
            onFilterClick = homeViewModel::toggleBottomSheet,
            toggleSelectionSearchFocus = homeViewModel::toggleSearch,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeMainBar(
    query: String,
    isSearchMode: Boolean,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onMenuClick: () -> Unit,
    onFilterClick: () -> Unit,
    toggleSelectionSearchFocus: () -> Unit,
    modifier: Modifier = Modifier
) {

    AnimatedContent(
        targetState = isSearchMode,
        transitionSpec = {
            fadeIn() + scaleIn(initialScale = 0.8f) togetherWith fadeOut() + scaleOut(targetScale = 0.8f)
        }
    ) { state ->
        if (state) SearchBar(
            query = query,
            onQueryChange = onQueryChange, onClearQuery = onClearQuery, onCloseSearch = {
                toggleSelectionSearchFocus()
                onClearQuery()
            }) else TopAppBar(
            title = {
                Text(stringResource(R.string.app_name))
            }, navigationIcon = {
                CustomToolTip("Menu") {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Rounded.Menu, null)
                    }
                }
            }, actions = {
                CustomToolTip("Search") {
                    IconButton(onClick = toggleSelectionSearchFocus) {
                        Icon(Icons.Rounded.Search, null)
                    }
                }
                CustomToolTip("Filter") {
                    IconButton(onClick = onFilterClick) {
                        Icon(Icons.Rounded.FilterList, null)
                    }
                }
            }, modifier = modifier
        )
    }
}

@Composable
fun SelectionBar(
    query: String,
    selectedIds: Set<Int>,
    filterCount: Int,
    trigger: Trigger,
    hasAnyNonFavorite: Boolean,
    isSearchMode: Boolean,
    hasActiveFilters: Boolean,
    hasSelectionChanged: Boolean,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onFilterClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    navigateUp: () -> Unit,
    toggleSelectionSearchFocus: () -> Unit,
    onDiscardChanges: () -> Unit,
    toggleSelectionMode: () -> Unit,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {

    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }

    val onBack: () -> Unit = {
        if (hasSelectionChanged) showDiscardDialog = true
        else {
            if (trigger == Trigger.INIT) navHostController.navigateUp()
            toggleSelectionMode()
        }
    }

    BackHandler { onBack() }

    Column(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(TopAppBarDefaults.windowInsets.asPaddingValues())
                .height(TopAppBarDefaults.TopAppBarExpandedHeight)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomToolTip("Close") {
                    IconButton(onClick = onBack) {
                        val imageVector = Icons.Rounded.Close
                        Icon(imageVector, imageVector.name)
                    }
                }
                Text("${selectedIds.size}", style = MaterialTheme.typography.titleLarge)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.animateContentSize()
            ) {
                AnimatedVisibility(
                    visible = hasAnyNonFavorite && trigger != Trigger.INIT,
                    enter = fadeIn() + scaleIn(initialScale = 0.8f),
                    exit = fadeOut(animationSpec = tween(delayMillis = 1000)) + scaleOut(
                        targetScale = 0.8f,
                        animationSpec = tween(delayMillis = 1000)
                    )
                ) {
                    var isClicked by rememberSaveable { mutableStateOf(false) }
                    CustomToolTip("Favorite") {
                        IconButton(onClick = {
                            onFavoriteClick()
                            isClicked = true
                        }) {
                            if (isClicked) Icon(
                                imageVector = Icons.Rounded.Favorite,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            else Icon(
                                imageVector = Icons.Rounded.FavoriteBorder,
                                contentDescription = null
                            )
                        }
                    }
                }
                AnimatedContent(targetState = isSearchMode) { state ->
                    if (state) {
                        CustomToolTip("Search off") {
                            IconButton(onClick = toggleSelectionSearchFocus) {
                                Icon(Icons.Rounded.SearchOff, null)
                            }
                        }
                    } else {
                        CustomToolTip("Search") {
                            IconButton(onClick = toggleSelectionSearchFocus) {
                                Icon(Icons.Rounded.Search, null)
                            }
                        }
                    }
                }
                AnimatedVisibility(
                    visible = !isSearchMode,
                    enter = fadeIn() + scaleIn(initialScale = 0.8f),
                    exit = ExitTransition.None
                ) {
                    CustomToolTip("Filter") {
                        IconButton(onClick = onFilterClick) {
                            Icon(Icons.Rounded.FilterList, null)
                        }
                    }
                }
            }
        }
        AnimatedVisibility(visible = isSearchMode) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 8.dp
                    )
            ) {
                SearchBar(
                    query = query,
                    onQueryChange = onQueryChange,
                    onClearQuery = onClearQuery,
                    onCloseSearch = {
                        toggleSelectionSearchFocus()
                        onClearQuery()
                    },
                    isDefaultLayout = false,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(16.dp))
                BadgedBox(
                    badge = {
                        if (hasActiveFilters) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.tertiaryContainer,
                                        shape = CircleShape
                                    )
                            ) {
                                Text(
                                    text = filterCount.toString(),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                ) {
                    CustomToolTip("Filter") {
                        IconButton(onClick = onFilterClick) {
                            Icon(Icons.Rounded.FilterList, null)
                        }
                    }
                }
            }

        }
    }
    DiscardDialog(
        visible = showDiscardDialog,
        onConfirm = {
            onDiscardChanges()
            showDiscardDialog = false
            navigateUp()
            toggleSelectionMode()
        },
        onDismissRequest = { showDiscardDialog = false }
    )
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onCloseSearch: () -> Unit,
    modifier: Modifier = Modifier,
    isDefaultLayout: Boolean = true
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    BackHandler { onCloseSearch() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text("Search")
        },
        leadingIcon = {
            if (isDefaultLayout) {
                CustomToolTip("Back") {
                    IconButton(onClick = onCloseSearch) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "ArrowBack")
                    }
                }
            } else {
                CustomToolTip("Search") {
                    Icon(Icons.Rounded.Search, "Search")
                }
            }
        },
        trailingIcon = {
            CustomToolTip("Clear") {
                IconButton(onClick = onClearQuery) {
                    Icon(Icons.Rounded.Clear, "Clear")
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onQueryChange(query)
                keyboardController?.hide()
            }
        ),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = if (isDefaultLayout) Color.Transparent else Color.Unspecified,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isDefaultLayout) {
                    Modifier
                        .padding(TopAppBarDefaults.windowInsets.asPaddingValues())
                        .height(TopAppBarDefaults.TopAppBarExpandedHeight)

                } else Modifier
                    .windowInsetsPadding(WindowInsets())
                    .clip(MaterialTheme.shapes.large)
            )
            .focusRequester(focusRequester)

    )
}