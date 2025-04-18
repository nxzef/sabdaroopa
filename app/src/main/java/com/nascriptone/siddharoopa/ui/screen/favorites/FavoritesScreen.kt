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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.data.model.uiobj.FavoriteSabdaDetails
import com.nascriptone.siddharoopa.ui.component.CurrentState
import com.nascriptone.siddharoopa.ui.screen.Sound
import com.nascriptone.siddharoopa.ui.screen.TableCategory
import com.nascriptone.siddharoopa.viewmodel.SiddharoopaViewModel

@Composable
fun FavoritesScreen(
    viewModel: SiddharoopaViewModel,
    favoritesUIState: FavoritesScreenState,
    modifier: Modifier = Modifier
) {

    LaunchedEffect(Unit) {
        viewModel.fetchFavoriteSabda()
    }

    when (val result = favoritesUIState.result) {
        is ScreenState.Loading -> CurrentState {
            CircularProgressIndicator()
        }

        is ScreenState.Error -> CurrentState {
            Text(result.msg)
        }

        is ScreenState.Success -> FavoritesScreenContent(
            favoritesSabdaList = result.data, modifier = modifier
        )
    }
}


@Composable
fun FavoritesScreenContent(
    favoritesSabdaList: List<FavoriteSabdaDetails>, modifier: Modifier = Modifier
) {

    var isDialogOpened by rememberSaveable { mutableStateOf(false) }

    if (favoritesSabdaList.isNotEmpty()) {
        Surface {
            LazyColumn(
                modifier = modifier.padding(horizontal = 4.dp)
            ) {
                item {
                    Spacer(Modifier.height(16.dp))
                }
                items(favoritesSabdaList) { details ->
                    FavoritesSabdaCard(
                        onClick = {

                        }, onLongClick = {
                            Log.d("click", "LongClick Works!")
                        }, onHeartIconClick = {
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
                modifier = Modifier
                    .clip(MaterialTheme.shapes.extraLarge)
            )
        }
    } else {
        CurrentState {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Rounded.FavoriteBorder,
                    null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.surfaceTint
                )
                Spacer(Modifier.height(8.dp))
                Text("Favorites are empty!")
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavoritesSabdaCard(
    onLongClick: () -> Unit,
    onClick: () -> Unit,
    onHeartIconClick: () -> Unit,
    details: FavoriteSabdaDetails,
    modifier: Modifier = Modifier
) {
    val sabda = details.sabda
    val sabdaSkt = stringResource(R.string.sabda)
    val table = when (details.table) {
        TableCategory.General -> stringResource(R.string.general_category)
        TableCategory.Specific -> stringResource(R.string.specific_category)
    }
    val genderInSkt = when (sabda.gender) {
        stringResource(R.string.masculine_eng).lowercase() -> stringResource(R.string.masculine_skt)
        stringResource(R.string.feminine_eng).lowercase() -> stringResource(R.string.feminine_skt)
        else -> stringResource(R.string.neuter_skt)
    }

    val sound = Sound.valueOf(sabda.sound.uppercase())
    val detailedText = "${sabda.anta} $genderInSkt $sabdaSkt"

    Card(
        modifier = modifier.combinedClickable(
            onClick = onClick, onLongClick = onLongClick
        ),
        shape = MaterialTheme.shapes.large,
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
                    Icon(Icons.Rounded.Favorite, null, tint = MaterialTheme.colorScheme.secondary)
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
                    Text("See table")
                    Icon(
                        Icons.AutoMirrored.Rounded.KeyboardArrowRight, null
                    )
                }
            }
        }
    }
}

@Composable
fun DeletionDialog(
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
                    Text("You're about to remove this from your favorite items. Are you sure?")
                    Spacer(Modifier.height(36.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {}) {
                            Text("Cancel")
                        }
                        Spacer(Modifier.width(8.dp))
                        TextButton(onClick = {}) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}

