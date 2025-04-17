package com.nascriptone.siddharoopa.ui.screen.favorites

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
        is ScreenState.Loading -> {
            Log.d("fav_data", "Loading...")
            CurrentState {
                CircularProgressIndicator()
            }
        }

        is ScreenState.Error -> {
            Log.d("fav_data", result.msg)
            CurrentState {
                Text(result.msg)
            }
        }

        is ScreenState.Success -> {
            Log.d("fav_data", "${result.data}")
            if (result.data.isEmpty()) {
                Text("Fetched Successfully, but data is empty.")
            } else {
                Text("${result.data.size} data retrieved")
            }
        }
    }
}


@Composable
fun FavoritesScreenContent(
    modifier: Modifier = Modifier
) {
    Surface {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(24.dp))
            repeat(7) {
                FavoritesSabdaCard(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                )
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
        FavoritesScreenContent()
    }
}

@Preview
@Composable
fun FavoritesSabdaCardPreview() {
    SiddharoopaTheme {
        FavoritesSabdaCard()
    }
}