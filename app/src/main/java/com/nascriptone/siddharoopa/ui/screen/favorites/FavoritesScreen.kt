package com.nascriptone.siddharoopa.ui.screen.favorites

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.data.model.uiobj.EntireSabda
import com.nascriptone.siddharoopa.data.model.uiobj.Sound
import com.nascriptone.siddharoopa.data.model.uiobj.Table
import com.nascriptone.siddharoopa.ui.component.CurrentState
import com.nascriptone.siddharoopa.ui.screen.SiddharoopaRoutes
import com.nascriptone.siddharoopa.viewmodel.SiddharoopaViewModel

@Composable
fun FavoritesScreen(
    viewModel: SiddharoopaViewModel,
    navHostController: NavHostController,
    favoritesUIState: FavoritesScreenState,
    modifier: Modifier = Modifier
) {

    val favoriteSabdaList = favoritesUIState.favoriteList

    if (favoriteSabdaList.isEmpty()) {
        CurrentState {
            Text("There is nothing!")
        }
    } else {
        FavoritesScreenContent(
            viewModel = viewModel,
            navHostController = navHostController,
            favoritesUIState = favoritesUIState,
            favoritesSabdaList = favoriteSabdaList,
            modifier = modifier
        )
    }
}


@Composable
fun FavoritesScreenContent(
    viewModel: SiddharoopaViewModel,
    navHostController: NavHostController,
    favoritesUIState: FavoritesScreenState,
    favoritesSabdaList: List<EntireSabda>,
    modifier: Modifier = Modifier
) {

    var isDialogOpened by rememberSaveable { mutableStateOf(false) }
    val sabdaToRemove = favoritesUIState.sabdaToRemove

    val filteredList = favoritesSabdaList.sortedByDescending { it.isFavorite.timestamp }

    Surface {
        LazyColumn(
            modifier = modifier.padding(horizontal = 4.dp)
        ) {
            item {
                Spacer(Modifier.height(16.dp))
            }
            items(filteredList) { details ->
                FavoritesSabdaCard(
                    onClick = {
                        viewModel.updateSelectedSabda(details)
                        navHostController.navigate(SiddharoopaRoutes.Table.name)
                    }, onLongClick = {
                        Log.d("click", "LongClick Works!")
                    }, onHeartIconClick = {
                        viewModel.updateSabdaToRemove(details)
                        isDialogOpened = !isDialogOpened
                    }, details = details, modifier = Modifier.padding(8.dp)
                )
            }
            item {
                Spacer(Modifier.height(28.dp))
            }
        }

        DeletionDialog(
            isOpen = isDialogOpened,
            onDismissRequest = {
                isDialogOpened = false
            },
            onConfirm = {
                if (sabdaToRemove != null) {
                    viewModel.toggleFavoriteSabda(sabdaToRemove)
                }
                isDialogOpened = false
            },
            modifier = Modifier
                .clip(MaterialTheme.shapes.extraLarge)
        )
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavoritesSabdaCard(
    onLongClick: () -> Unit,
    onClick: () -> Unit,
    onHeartIconClick: () -> Unit,
    details: EntireSabda,
    modifier: Modifier = Modifier
) {
    val sabda = details.sabda
    val sabdaSkt = stringResource(R.string.sabda)
    val table = when (details.table) {
        Table.GENERAL -> stringResource(R.string.general_table)
        Table.SPECIFIC -> stringResource(R.string.specific_table)
    }
    val genderInSkt = when (sabda.gender) {
        stringResource(R.string.masculine_eng).lowercase() -> stringResource(R.string.masculine_skt)
        stringResource(R.string.feminine_eng).lowercase() -> stringResource(R.string.feminine_skt)
        else -> stringResource(R.string.neuter_skt)
    }

    val sound = Sound.valueOf(sabda.sound.uppercase())
    val detailedText = "${sabda.anta} $genderInSkt $sabdaSkt"

    val heartIcon = painterResource(R.drawable.heart_minus_24px_1_)

    Card(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .combinedClickable(
                onClick = onClick, onLongClick = onLongClick
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(sabda.word, style = MaterialTheme.typography.headlineMedium)
                IconButton(onClick = onHeartIconClick) {
                    Icon(heartIcon, null, tint = MaterialTheme.colorScheme.secondary)
                }
            }
            Text(
                detailedText,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7F)
            )
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "$table (${stringResource(sound.skt)})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7F)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick) {
                        Text("See table")
                    }
                    Icon(
                        Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun DeletionDialog(
    onConfirm: () -> Unit,
    isOpen: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {

    if (isOpen) {
        Dialog(onDismissRequest) {
            Box(
                modifier = modifier
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(280.dp, 560.dp)
                        .padding(24.dp)
                ) {
                    Text("Do you want to delete this from your favorites?")
                    Spacer(Modifier.height(36.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onDismissRequest) {
                            Text("Cancel")
                        }
                        Spacer(Modifier.width(8.dp))
                        TextButton(onConfirm) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}

