package com.nascriptone.siddharoopa.ui.screen.favorites

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.TableView
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.ui.component.CurrentState
import com.nascriptone.siddharoopa.ui.component.CustomDialog
import com.nascriptone.siddharoopa.ui.component.CustomDialogDescription
import com.nascriptone.siddharoopa.ui.component.CustomDialogHead
import com.nascriptone.siddharoopa.ui.component.CustomToolTip
import com.nascriptone.siddharoopa.ui.component.getSupportingText
import com.nascriptone.siddharoopa.ui.screen.Routes

@Composable
fun FavoritesScreen(
    navHostController: NavHostController,
    favoritesViewModel: FavoritesViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by favoritesViewModel.uiState.collectAsStateWithLifecycle()
    val favorites = favoritesViewModel.favorites.collectAsLazyPagingItems()
    FavoritesScreenContent(
        uiState = uiState,
        favorites = favorites,
        navHostController = navHostController,
        favoritesViewModel = favoritesViewModel,
        modifier = modifier
    )
}

@Composable
fun FavoritesScreenContent(
    uiState: FavoritesState,
    favorites: LazyPagingItems<Sabda>,
    navHostController: NavHostController,
    favoritesViewModel: FavoritesViewModel,
    modifier: Modifier = Modifier,
) {

    var currentDrop by rememberSaveable { mutableStateOf<Int?>(null) }
    val defaultDeleteState = remember { DeleteDialogState(null, false) }
    var deleteItem by rememberSaveable(
        stateSaver = listSaver(
            save = {
                listOf(it.id, it.visible)
            },
            restore = {
                DeleteDialogState(it[0] as Int?, it[1] as Boolean)
            }
        )
    ) { mutableStateOf(defaultDeleteState) }

    LaunchedEffect(uiState.isSelectMode) { currentDrop = null }

    Surface {
        LazyColumn(
            modifier = modifier.animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(Modifier.height(16.dp))
            }

            items(
                count = favorites.itemCount,
                key = favorites.itemKey { it.id }
            ) { index ->
                val sabda: Sabda? = favorites[index]
                sabda?.let { sabda ->
                    val isInSelected = sabda.id in uiState.selectedIds
                    val isSelectMode = uiState.isSelectMode
                    FavoriteCard(
                        sabda = sabda,
                        isSelectMode = isSelectMode,
                        isInSelected = isInSelected,
                        currentDrop = currentDrop,
                        onTableClick = { id ->
                            navHostController.navigate(Routes.Table.name.plus("/$id")) {
                                launchSingleTop = true
                            }
                        },
                        onQuizClick = {},
                        onDeleteClick = { id ->
                            deleteItem = DeleteDialogState(
                                id = id,
                                visible = true
                            )
                        },
                        onCardClick = { id ->
                            if (uiState.isSelectMode) {
                                favoritesViewModel.toggleSelectedId(id)
                            } else {
                                currentDrop = if (currentDrop == id) null else id
                            }
                        },
                        onCardLongClick = favoritesViewModel::toggleSelectedId,
                        modifier = Modifier
                            .animateContentSize()
                            .animateItem()
                    )
                }
            }

            favorites.apply {
                when {
                    loadState.refresh is LoadState.Loading || loadState.append is LoadState.Loading -> {
                        item { CurrentState(Modifier) { CircularProgressIndicator() } }
                    }

                    loadState.refresh is LoadState.Error || loadState.append is LoadState.Error -> {
                        item { CurrentState { Text("Error loading more") } }
                    }
                }
            }

            item {
                Spacer(Modifier.height(28.dp))
            }
        }

        DeleteDialog(
            visible = deleteItem.visible,
            onDismissRequest = {
                deleteItem = defaultDeleteState
            },
            onConfirm = {
                favoritesViewModel.toggleFavoriteSabda(deleteItem.id!!)
                deleteItem = defaultDeleteState
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteCard(
    sabda: Sabda,
    isSelectMode: Boolean,
    isInSelected: Boolean,
    currentDrop: Int?,
    onTableClick: (Int) -> Unit,
    onQuizClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit,
    onCardClick: (Int) -> Unit,
    onCardLongClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDropped = currentDrop == sabda.id
    val haptic = LocalHapticFeedback.current
    val cardOptions: List<CardOption> = remember {
        listOf(
            CardOption(
                name = "Table",
                icon = Icons.Rounded.TableView,
            ),
            CardOption(
                name = "Quiz",
                icon = Icons.Rounded.Quiz,
            ),
            CardOption(
                name = "Delete",
                icon = Icons.Rounded.Delete
            )
        )
    }
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .then(
                if (isInSelected) {
                    Modifier.border(
                        border = BorderStroke(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.tertiary
                        ),
                        shape = MaterialTheme.shapes.large
                    )
                } else Modifier
            )
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.large
            )
            .clip(MaterialTheme.shapes.large)
            .combinedClickable(
                onClick = { onCardClick(sabda.id) },
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onCardLongClick(sabda.id)
                }
            )
            .then(modifier)
    ) {
        Column(
            modifier = Modifier.padding(
                start = 16.dp,
                top = 16.dp,
                end = 8.dp,
                bottom = 8.dp
            )
        ) {
            Text(
                text = "${sabda.word.plus("ः")}\t(${sabda.meaning})",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = getSupportingText(sabda),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${stringResource(sabda.sound.skt)}\t\u2022\t${stringResource(sabda.category.skt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7F),
                )

                CustomToolTip(if (isDropped) "Drop Up" else "Drop Down") {
                    IconButton(
                        enabled = !isSelectMode,
                        onClick = { onCardClick(sabda.id) }
                    ) {
                        val rotate by animateFloatAsState(
                            targetValue = if (isDropped) 180f else 0f,
                            animationSpec = tween(200)
                        )
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.rotate(rotate)
                        )
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = isDropped,
            enter = fadeIn() + expandVertically(
                animationSpec = tween(60)
            ),
            exit = fadeOut() + shrinkVertically(
                animationSpec = tween(60)
            )
        ) {
            OptionsComponent(
                onTableClick = { onTableClick(sabda.id) },
                onQuizClick = { onQuizClick(sabda.id) },
                onDeleteClick = { onDeleteClick(sabda.id) },
                cardOptions = cardOptions,
                modifier = Modifier
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsComponent(
    onTableClick: () -> Unit,
    onQuizClick: () -> Unit,
    onDeleteClick: () -> Unit,
    cardOptions: List<CardOption>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(
            start = 16.dp,
            top = 0.dp,
            end = 16.dp,
            bottom = 16.dp
        )
    ) {
        cardOptions.forEachIndexed { index, option ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clip(MaterialTheme.shapes.large)
                    .clickable(
                        onClick = when (index) {
                            0 -> onTableClick
                            1 -> onQuizClick
                            else -> onDeleteClick
                        }
                    )
                    .padding(8.dp)
            ) {
                val color: Color = if (index == 2) Color.Red.copy(
                    green = 0.3f,
                    blue = 0.3f
                )
                else LocalContentColor.current
                CustomToolTip(option.name) {
                    Icon(
                        imageVector = option.icon,
                        tint = color,
                        contentDescription = null,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = option.name,
                    color = color,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
fun DeleteDialog(
    visible: Boolean,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    CustomDialog(
        visible = visible,
        onDismissRequest = onDismissRequest,
        head = { CustomDialogHead(text = "Remove Sabda?") },
        description = {
            CustomDialogDescription(text = "Do you want to remove this from your favorites?")
        },
        showDefaultAction = true,
        onConfirm = onConfirm,
        onCancel = onDismissRequest,
        modifier = modifier
    )
}

data class CardOption(
    val name: String,
    val icon: ImageVector,
)

data class DeleteDialogState(
    val id: Int? = null,
    val visible: Boolean = false
)
