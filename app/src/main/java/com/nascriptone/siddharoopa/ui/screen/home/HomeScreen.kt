package com.nascriptone.siddharoopa.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.TableView
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.nascriptone.siddharoopa.data.model.Category
import com.nascriptone.siddharoopa.data.model.Gender
import com.nascriptone.siddharoopa.data.model.Sound
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.ui.component.CustomToolTip
import com.nascriptone.siddharoopa.ui.component.getSupportingText
import com.nascriptone.siddharoopa.ui.state.Filter
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onItemClick: (Int) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val sabdaList = homeViewModel.sabda.collectAsLazyPagingItems()
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        homeViewModel.uiEvents.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        val visible =
            uiState.filter.isActive && !uiState.isSelectMode || uiState.filter.isActive && uiState.isSelectMode && !uiState.isSearchMode
        AnimatedVisibility(visible = visible) {
            ActiveFiltersRow(
                filter = uiState.filter,
                onClearFilters = homeViewModel::onClearFilter,
                onFilterClick = homeViewModel::toggleBottomSheet
            )
        }
        HomeScreenList(
            sabdaList = sabdaList,
            selectedIds = uiState.selectedIds,
            isSelectMode = uiState.isSelectMode,
            fabVisible = uiState.selectedIds.isNotEmpty(),
            onItemClick = { id ->
                if (uiState.isSelectMode) {
                    homeViewModel.toggleSelectedId(id)
                } else onItemClick(id)
            },
            onItemLongClick = homeViewModel::toggleSelectedId,
            onTableViewClick = onItemClick,
            modifier = modifier
        )
    }
    HomeBottomSheet(
        visible = uiState.bottomSheetVisible,
        currentFilter = uiState.filter,
        onDismissRequest = homeViewModel::toggleBottomSheet,
        onApplyFilter = homeViewModel::updateFilter
    )
}


@Composable
fun HomeScreenList(
    sabdaList: LazyPagingItems<Sabda>,
    selectedIds: Set<Int>,
    isSelectMode: Boolean,
    fabVisible: Boolean,
    onItemClick: (Int) -> Unit,
    onItemLongClick: (Int) -> Unit,
    onTableViewClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.ime.exclude(WindowInsets.navigationBars))
    ) {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { Spacer(Modifier.height(8.dp)) }
            items(
                count = sabdaList.itemCount,
                key = sabdaList.itemKey { it.id },
            ) { index: Int ->
                val sabda: Sabda? = sabdaList[index]
                sabda?.let { sabda ->
                    SabdaItem(
                        sabda = sabda,
                        isInSelected = sabda.id in selectedIds,
                        isSelectMode = isSelectMode,
                        onClick = onItemClick,
                        onLongClick = onItemLongClick,
                        onTableViewClick = onTableViewClick,
                        modifier = Modifier
                            .animateContentSize()
                            .animateItem()
                    )
                }
            }

            if (sabdaList.loadState.append == LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            item {
                Spacer(Modifier.height(TopAppBarDefaults.TopAppBarExpandedHeight + 16.dp))
            }
        }
        if (sabdaList.loadState.refresh is LoadState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        if (sabdaList.loadState.refresh is LoadState.NotLoading && sabdaList.itemCount == 0) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.SearchOff,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "No results found",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Try different search terms or filters",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        AnimatedVisibility(
            visible = fabVisible,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-16).dp, y = (-16).dp)
        ) {
            FloatingActionButton(onClick = {}) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(Icons.Rounded.Quiz, null)
                    Spacer(Modifier.width(12.dp))
                    Text("Take Quiz")
                }
            }
        }
    }
}

@Composable
fun SabdaItem(
    sabda: Sabda,
    isSelectMode: Boolean,
    isInSelected: Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    onTableViewClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isInSelected) {
                    Modifier.border(
                        border = BorderStroke(
                            width = 2.dp, color = MaterialTheme.colorScheme.tertiary
                        ), shape = MaterialTheme.shapes.large
                    )
                } else Modifier
            )
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.large
            )
            .clip(MaterialTheme.shapes.large)
            .combinedClickable(
                onClick = { onClick(sabda.id) },
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongClick(sabda.id)
                }
            )
            .padding(
                start = 12.dp,
                top = 12.dp,
                end = 4.dp,
                bottom = 4.dp
            )

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = sabda.word,
                style = MaterialTheme.typography.headlineSmall,
            )
            if (isInSelected) Icon(
                imageVector = Icons.Filled.CheckCircle,
                tint = MaterialTheme.colorScheme.tertiary,
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = sabda.translit)
            Text("\u2022", modifier = Modifier.padding(horizontal = 8.dp))
            Text(
                text = sabda.meaning, color = MaterialTheme.colorScheme.onSurfaceVariant
            )

        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (sabda.isFavorite) {
                    Icon(
                        imageVector = Icons.Rounded.Favorite,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.surfaceTint,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    text = getSupportingText(sabda),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = .7f)
                )
            }

            AnimatedVisibility(
                visible = isSelectMode,
                enter = fadeIn() + scaleIn(initialScale = 0.8f),
                exit = fadeOut() + scaleOut(targetScale = 0.8f)
            ) {
                CustomToolTip("Table") {
                    IconButton(onClick = { onTableViewClick(sabda.id) }) {
                        Icon(Icons.Rounded.TableView, null)
                    }
                }
            }
        }
        if (!isSelectMode) Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun ActiveFiltersRow(
    filter: Filter,
    onClearFilters: () -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = true,
                onClick = onClearFilters,
                label = { Text("Clear all") },
                leadingIcon = { Icon(Icons.Rounded.Close, contentDescription = null) })
        }

        filter.category?.let { category ->
            item {
                FilterChip(
                    selected = true,
                    onClick = onFilterClick,
                    label = { Text(stringResource(category.skt)) },
                    trailingIcon = {
                        Icon(
                            Icons.Rounded.KeyboardArrowDown, contentDescription = null
                        )
                    })
            }
        }

        filter.sound?.let { sound ->
            item {
                FilterChip(
                    selected = true,
                    onClick = onFilterClick,
                    label = { Text(stringResource(sound.skt)) },
                    trailingIcon = {
                        Icon(
                            Icons.Rounded.KeyboardArrowDown, contentDescription = null
                        )
                    })
            }
        }

        filter.gender?.let { gender ->
            item {
                FilterChip(
                    selected = true,
                    onClick = onFilterClick,
                    label = { Text(stringResource(gender.skt)) },
                    trailingIcon = {
                        Icon(
                            Icons.Rounded.KeyboardArrowDown, contentDescription = null
                        )
                    })
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeBottomSheet(
    visible: Boolean,
    currentFilter: Filter,
    onDismissRequest: () -> Unit,
    onApplyFilter: (Filter) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!visible) return
    val sheetState = rememberModalBottomSheetState(true)
    var tempFilter by remember { mutableStateOf(currentFilter) }
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest, sheetState = sheetState, modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Filter Options", style = MaterialTheme.typography.titleLarge
            )

            // Category filter
            FilterSection(
                title = "Category",
                options = Category.entries,
                selectedOption = tempFilter.category,
                onOptionSelected = { tempFilter = tempFilter.copy(category = it) },
                onClear = {
                    tempFilter = tempFilter.copy(category = null)
                }) { category -> stringResource(category.skt) }

            // Sound filter
            FilterSection(
                title = "Sound",
                options = Sound.entries,
                selectedOption = tempFilter.sound,
                onOptionSelected = { tempFilter = tempFilter.copy(sound = it) },
                onClear = { tempFilter = tempFilter.copy(sound = null) }) { sound ->
                stringResource(
                    sound.skt
                )
            }

            // Gender filter
            FilterSection(
                title = "Gender",
                options = Gender.entries,
                selectedOption = tempFilter.gender,
                onOptionSelected = { tempFilter = tempFilter.copy(gender = it) },
                onClear = {
                    tempFilter = tempFilter.copy(gender = null)
                }) { gender -> stringResource(gender.skt) }

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            onDismissRequest()
                        }
                    }, modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        scope.launch {
                            onApplyFilter(tempFilter)
                            sheetState.hide()
                            onDismissRequest()
                        }
                    }, modifier = Modifier.weight(1f)
                ) {
                    Text("Apply")
                }
            }
        }
    }
}


@Composable
fun <T> FilterSection(
    title: String,
    options: List<T>,
    selectedOption: T?,
    onOptionSelected: (T?) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    labelMapper: @Composable (T) -> String
) {
    Column(
        modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title, style = MaterialTheme.typography.titleMedium
            )
            if (selectedOption != null) {
                TextButton(onClick = onClear) {
                    Text("Clear")
                }
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(options) { option ->
                FilterChip(selected = option == selectedOption, onClick = {
                    onOptionSelected(if (option == selectedOption) null else option)
                }, label = { Text(labelMapper(option)) })
            }
        }
    }
}