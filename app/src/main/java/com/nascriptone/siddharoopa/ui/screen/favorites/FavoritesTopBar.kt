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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Deselect
import androidx.compose.material.icons.rounded.Mode
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.SelectAll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nascriptone.siddharoopa.ui.component.CustomDialog
import com.nascriptone.siddharoopa.ui.component.CustomDialogDescription
import com.nascriptone.siddharoopa.ui.component.CustomDialogHead
import com.nascriptone.siddharoopa.ui.component.CustomToolTip
import com.nascriptone.siddharoopa.ui.component.DialogLayout
import com.nascriptone.siddharoopa.ui.screen.Navigation
import com.nascriptone.siddharoopa.utils.extensions.sharedViewModelOrNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesTopBar(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val favoritesViewModel: FavoritesViewModel? =
        navHostController.sharedViewModelOrNull(Navigation.Favorites.name)
    if (favoritesViewModel == null) return
    val uiState by favoritesViewModel.uiState.collectAsStateWithLifecycle()
    AnimatedContent(
        targetState = uiState.isSelectMode, transitionSpec = {
            fadeIn() + scaleIn(initialScale = 0.8f) togetherWith fadeOut() + scaleOut(targetScale = 1.2f)
        }) { isSelectMode ->
        if (isSelectMode) FavoriteActionTopBar(favoritesViewModel = favoritesViewModel)
        else TopAppBar(
            title = {
                Text("Favorites")
            }, navigationIcon = {
                CustomToolTip("Back") {
                    IconButton(onClick = navHostController::navigateUp) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                    }
                }
            }, actions = {
                if (uiState.totalIds.isNotEmpty()) {
                    CustomToolTip("Select") {
                        IconButton(onClick = {
                            favoritesViewModel.toggleSelectionMode(trigger = Trigger.TOOLBAR)
                        }) {
                            Icon(Icons.Rounded.Mode, null)
                        }
                    }
                }
            }, modifier = modifier
        )
    }
    uiState.transferState?.let { transferState ->
        TransferDialog(
            transferState = transferState,
            onDismissRequest = favoritesViewModel::transferDialogDismiss,
            onRetryClick = favoritesViewModel::onTakeQuizClick,
            onSuccess = {
                navHostController.navigate(Navigation.Quiz.name) {
                    popUpTo(Navigation.Favorites.name) {
                        inclusive = true
                    }
                }
                favoritesViewModel.toggleSelectionMode()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteActionTopBar(
    favoritesViewModel: FavoritesViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by favoritesViewModel.uiState.collectAsStateWithLifecycle()
    var deleteDialogVisible by rememberSaveable { mutableStateOf(false) }

    BackHandler(onBack = favoritesViewModel::toggleSelectionMode)

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
                    IconButton(onClick = favoritesViewModel::toggleSelectionMode) {
                        val imageVector = Icons.Rounded.Close
                        Icon(imageVector, imageVector.name)
                    }
                }
                Text(
                    "${uiState.selectedIds.size}", style = MaterialTheme.typography.titleLarge
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            ) {
                val isNotEmpty = uiState.selectedIds.isNotEmpty()
                AnimatedVisibility(
                    visible = isNotEmpty,
                    enter = fadeIn() + scaleIn(initialScale = 0.8f),
                    exit = fadeOut() + scaleOut(targetScale = 1.2f)
                ) {
                    CustomToolTip("Quiz") {
                        IconButton(onClick = favoritesViewModel::onTakeQuizClick) {
                            val imageVector = Icons.Rounded.Quiz
                            Icon(imageVector, imageVector.name)
                        }
                    }
                    AnimatedVisibility(true) { }
                    AnimatedVisibility(false) { }
                }
                AnimatedVisibility(
                    visible = isNotEmpty && uiState.trigger != Trigger.INIT,
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
                AnimatedContent(
                    targetState = uiState.areAllSelected
                ) { state ->
                    if (state) CustomToolTip("Deselect All") {
                        IconButton(onClick = favoritesViewModel::toggleFavoriteSelectAll) {
                            val imageVector = Icons.Rounded.Deselect
                            Icon(imageVector, imageVector.name)
                        }
                    } else CustomToolTip("Select All") {
                        IconButton(onClick = favoritesViewModel::toggleFavoriteSelectAll) {
                            val imageVector = Icons.Rounded.SelectAll
                            Icon(imageVector, imageVector.name)
                        }
                    }
                }
            }
        }
        DeleteAllDialog(
            visible = deleteDialogVisible,
            onConfirm = {
                favoritesViewModel.deleteAllItemFromFavorite()
                favoritesViewModel.toggleSelectionMode()
                deleteDialogVisible = false
            },
            onDismissRequest = { deleteDialogVisible = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferDialog(
    transferState: TransferState,
    onDismissRequest: () -> Unit,
    onRetryClick: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (transferState is TransferState.Success) LaunchedEffect(Unit) { onSuccess() }
    else BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        DialogLayout {
            when (transferState) {
                is TransferState.Loading -> Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(20.dp)
                ) {
                    CircularProgressIndicator()
                    Spacer(Modifier.width(16.dp))
                    Text("Updating...")
                }

                is TransferState.Error -> Column(modifier = Modifier.padding(16.dp)) {
                    CustomDialogHead("Error")
                    Spacer(Modifier.height(4.dp))
                    CustomDialogDescription(transferState.message)
                    Spacer(Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = onDismissRequest) {
                            Text("Cancel")
                        }
                        Spacer(Modifier.width(8.dp))
                        TextButton(onClick = onRetryClick) {
                            Text("Retry")
                        }
                    }
                }

                else -> Unit
            }
        }
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