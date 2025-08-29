package com.nascriptone.siddharoopa.ui.screen.favorites

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Deselect
import androidx.compose.material.icons.rounded.Mode
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.SelectAll
import androidx.compose.material.icons.rounded.TableView
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nascriptone.siddharoopa.ui.component.CustomDialog
import com.nascriptone.siddharoopa.ui.component.CustomDialogDescription
import com.nascriptone.siddharoopa.ui.component.CustomDialogHead
import com.nascriptone.siddharoopa.ui.component.CustomToolTip
import com.nascriptone.siddharoopa.viewmodel.SiddharoopaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesTopBar(
    onBackPress: () -> Unit,
    onTableClick: (Int) -> Unit,
    viewModel: SiddharoopaViewModel,
    favoriteUIState: FavoritesState,
    modifier: Modifier = Modifier
) {
    val toggleSelectionMode: () -> Unit = viewModel::toggleSelectionMode
    AnimatedContent(
        targetState = favoriteUIState.isSelectMode,
        transitionSpec = {
            fadeIn() + scaleIn(initialScale = 0.8f) togetherWith fadeOut() + scaleOut(targetScale = 1.2f)
        }
    ) { isSelectMode ->
        if (isSelectMode) {
            FavoriteActionTopBar(
                onClose = toggleSelectionMode,
                onTableClick = onTableClick,
                viewModel = viewModel,
                favoritesUIState = favoriteUIState
            )
        } else {
            TopAppBar(
                title = {
                    Text("Favorites")
                },
                navigationIcon = {
                    CustomToolTip("Back") {
                        IconButton(onClick = onBackPress) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                        }
                    }
                },
                actions = {
                    CustomToolTip("Select") {
                        IconButton(onClick = toggleSelectionMode) {
                            Icon(Icons.Rounded.Mode, null)
                        }
                    }
                },
                modifier = modifier
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteActionTopBar(
    onClose: () -> Unit,
    onTableClick: (Int) -> Unit,
    viewModel: SiddharoopaViewModel,
    favoritesUIState: FavoritesState,
    modifier: Modifier = Modifier
) {
    var deleteDialogVisible by rememberSaveable { mutableStateOf(false) }
    val toggleSelectAll: () -> Unit = viewModel::toggleSelectAll

    BackHandler(onBack = onClose)

    Surface {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .padding(TopAppBarDefaults.windowInsets.asPaddingValues())
                .height(TopAppBarDefaults.TopAppBarExpandedHeight)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CustomToolTip("Close") {
                    IconButton(onClick = onClose) {
                        val imageVector = Icons.Rounded.Close
                        Icon(imageVector, imageVector.name)
                    }
                }
                Text(
                    "${favoritesUIState.selectedIds.size}",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
            ) {
                val isNotEmpty = favoritesUIState.selectedIds.isNotEmpty()
                AnimatedVisibility(
                    visible = favoritesUIState.selectedIds.size == 1,
                    enter = fadeIn() + scaleIn(initialScale = 0.8f),
                    exit = fadeOut() + scaleOut(targetScale = 1.2f)
                ) {
                    CustomToolTip("Table") {
                        IconButton(onClick = { onTableClick(favoritesUIState.selectedIds.first()) }) {
                            val imageVector = Icons.Rounded.TableView
                            Icon(imageVector, imageVector.name)
                        }
                    }
                }
                AnimatedVisibility(
                    visible = isNotEmpty,
                    enter = fadeIn() + scaleIn(initialScale = 0.8f),
                    exit = fadeOut() + scaleOut(targetScale = 1.2f)
                ) {
                    CustomToolTip("Quiz") {
                        IconButton(onClick = {}) {
                            val imageVector = Icons.Rounded.Quiz
                            Icon(imageVector, imageVector.name)
                        }
                    }
                }
                AnimatedVisibility(
                    visible = isNotEmpty,
                    enter = fadeIn() + scaleIn(initialScale = 0.8f),
                    exit = fadeOut() + scaleOut(targetScale = 1.2f)
                ) {
                    CustomToolTip("Delete") {
                        IconButton(onClick = { deleteDialogVisible = true }) {
                            val imageVector = Icons.Rounded.Delete
                            Icon(imageVector, imageVector.name)
                        }
                    }
                }
                AnimatedContent(targetState = favoritesUIState.areAllSelected) { state ->
                    if (state) {
                        CustomToolTip("Deselect All") {
                            IconButton(onClick = toggleSelectAll) {
                                val imageVector = Icons.Rounded.Deselect
                                Icon(imageVector, imageVector.name)
                            }
                        }
                    } else {
                        CustomToolTip("Select All") {
                            IconButton(onClick = toggleSelectAll) {
                                val imageVector = Icons.Rounded.SelectAll
                                Icon(imageVector, imageVector.name)
                            }
                        }
                    }
                }
            }
        }
        DeleteAllDialog(
            visible = deleteDialogVisible,
            onConfirm = {
                viewModel.deleteAllItemFromFavorite()
                deleteDialogVisible = false
            },
            onDismissRequest = { deleteDialogVisible = false },
        )
    }
}

@Composable
fun DeleteAllDialog(
    visible: Boolean,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    CustomDialog(
        visible = visible,
        onDismissRequest = onDismissRequest,
        head = {
            CustomDialogHead("Delete Items?")
        },
        description = {
            CustomDialogDescription(
                "Confirm will delete all these selected items from favorites."
            )
        },
        showDefaultAction = true,
        onConfirm = onConfirm,
        onCancel = onDismissRequest,
        modifier = modifier
    )
}