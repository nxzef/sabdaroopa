package com.nascriptone.siddharoopa.ui.screen.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nascriptone.siddharoopa.data.model.uiobj.FavoriteSabdaDetails
import com.nascriptone.siddharoopa.ui.component.CurrentState
import com.nascriptone.siddharoopa.ui.theme.SiddharoopaTheme
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
            favoritesSabdaList = result.data,
            modifier = modifier
        )
    }
}


@Composable
fun FavoritesScreenContent(
    favoritesSabdaList: List<FavoriteSabdaDetails>,
    modifier: Modifier = Modifier
) {
    if (favoritesSabdaList.isNotEmpty()) {
        Surface {
            LazyColumn(
                modifier = modifier
                    .padding(12.dp)
            ) {
                items(favoritesSabdaList) { item ->
                    Text(item.sabda.word, style = MaterialTheme.typography.headlineMedium)
                }
            }
        }
    } else {
        CurrentState {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Rounded.FavoriteBorder, null, modifier = Modifier
                    .size(48.dp), tint = MaterialTheme.colorScheme.surfaceTint)
                Spacer(Modifier.height(8.dp))
                Text("Favorites are empty!")
            }
        }
    }
}

@Composable
fun FavoritesSabdaCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        onClick = {

        },
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
        ) {
            Text("Favorite", style = MaterialTheme.typography.titleLarge)
            Text(
                "This is a small demo text",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.outline
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Masculine")
                Row {
                    Text("See table")
                    Icon(
                        Icons.AutoMirrored.Rounded.KeyboardArrowRight, null
                    )
                }
            }

        }
    }
}


@Preview
@Composable
fun FavoriteScreenContentPreview() {
    SiddharoopaTheme(darkTheme = true) {
        FavoritesScreenContent(emptyList())
    }
}

@Preview
@Composable
fun FavoritesSabdaCardPreview() {
    SiddharoopaTheme {
        FavoritesSabdaCard()
    }
}