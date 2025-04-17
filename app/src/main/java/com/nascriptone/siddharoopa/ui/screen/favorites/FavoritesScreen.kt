package com.nascriptone.siddharoopa.ui.screen.favorites

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nascriptone.siddharoopa.ui.theme.SiddharoopaTheme

@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier
) {
    FavoritesScreenContent(
        modifier = modifier
    )
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