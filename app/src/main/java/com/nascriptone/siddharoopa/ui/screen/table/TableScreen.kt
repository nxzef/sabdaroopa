package com.nascriptone.siddharoopa.ui.screen.table

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.data.model.Declension
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.ui.component.CurrentState
import com.nascriptone.siddharoopa.ui.component.getSupportingText
import kotlinx.coroutines.launch

@Composable
fun TableScreen(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    tableViewModel: TableViewModel = hiltViewModel()
) {

    val uiState by tableViewModel.tableUIState.collectAsStateWithLifecycle()

    Crossfade(
        targetState = uiState
    ) { state ->
        when (state) {
            is FindState.Found -> TableScreenContent(
                sabda = state.sabda,
                tableViewModel = tableViewModel,
                snackbarHostState = snackbarHostState,
                modifier = modifier
            )

            is FindState.Finding -> CurrentState {
                CircularProgressIndicator()
            }

            is FindState.NotFound -> CurrentState {
                Text("Could not find declension table.")
            }

            is FindState.Error -> CurrentState {
                Text(state.message)
            }
        }
    }

}

@Composable
fun TableScreenContent(
    sabda: Sabda,
    tableViewModel: TableViewModel,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {

    val isItFavorite = sabda.isFavorite
    val label = if (isItFavorite) stringResource(R.string.remove_favorite_msg)
    else stringResource(R.string.add_favorite_msg)
    val message = if (isItFavorite) stringResource(R.string.removed_favorite_msg)
    else stringResource(R.string.added_favorite_msg)
    val scope = rememberCoroutineScope()

    Surface {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = sabda.word,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text(
                text = getSupportingText(sabda),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            DeclensionTable(sabda.declension)
            FavoriteView(
                label = label,
                isItFavorite = isItFavorite,
                onClick = {
                    scope.launch {
                        tableViewModel.toggleFavoriteSabda(sabda.id)
                        snackbarHostState.showSnackbar(message)
                    }
                }
            )
            Spacer(Modifier.height(TopAppBarDefaults.TopAppBarExpandedHeight))
        }
    }
}

@Composable
fun DeclensionTable(
    declension: Declension,
    modifier: Modifier = Modifier
) {
    val columnFirstItems = remember {
        listOf(
            R.string.vibakti,
            R.string.single,
            R.string.dual,
            R.string.plural
        )
    }
    val rowFirstItems = remember {
        listOf(
            R.string.nominative,
            R.string.vocative,
            R.string.accusative,
            R.string.instrumental,
            R.string.dative,
            R.string.ablative,
            R.string.genitive,
            R.string.locative
        )
    }
    OutlinedCard(
        modifier = modifier
            .padding(vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .height(54.dp)
        ) {
            columnFirstItems.forEachIndexed { i, e ->
                val cellValue = stringResource(e)
                key(i to e) {
                    DeclensionCell(cellValue, isPredefined = true, modifier = Modifier.weight(1f))
                }
                if (i != columnFirstItems.lastIndex) VerticalDivider()
            }
        }
        HorizontalDivider()
        val caseEntries = declension.entries.toList()
        caseEntries.forEachIndexed { i, case ->
            Row(
                modifier = Modifier.height(54.dp)
            ) {
                val predefinedId = rowFirstItems[i]
                val cellValue = stringResource(predefinedId)
                DeclensionCell(
                    value = cellValue,
                    isPredefined = true,
                    modifier = Modifier.weight(1F)
                )
                VerticalDivider()
                val formEntries = case.value.entries.toList()
                formEntries.forEachIndexed { i, form ->
                    val cellValue = form.value
                    DeclensionCell(value = cellValue, modifier = Modifier.weight(1f))
                    if (i != formEntries.lastIndex) VerticalDivider()
                }
            }
            if (i != caseEntries.lastIndex) HorizontalDivider()
        }
    }
}

@Composable
fun DeclensionCell(
    value: String?,
    modifier: Modifier = Modifier,
    isPredefined: Boolean = false,
) {
    val sepColor = if (isPredefined) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        MaterialTheme.colorScheme.surfaceContainerLow
    }
    val setFontWeight = if (isPredefined) {
        FontWeight.W700
    } else {
        LocalTextStyle.current.fontWeight
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(sepColor),
        contentAlignment = Alignment.Center
    ) {
        value?.let {
            Text(
                it,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = setFontWeight
            )
        }
    }
}

@Composable
fun FavoriteView(
    label: String,
    onClick: () -> Unit,
    isItFavorite: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick) {
                AnimatedContent(
                    targetState = isItFavorite
                ) { favorite ->
                    if (favorite) Icon(
                        Icons.Rounded.Favorite, null,
                        tint = MaterialTheme.colorScheme.surfaceTint
                    ) else Icon(
                        Icons.Rounded.FavoriteBorder, null
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            AnimatedContent(
                targetState = label
            ) { text -> Text(text) }
        }
    }
}

