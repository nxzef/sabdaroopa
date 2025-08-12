package com.nascriptone.siddharoopa.ui.screen.table

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.data.model.Declension
import com.nascriptone.siddharoopa.data.model.EntireSabda
import com.nascriptone.siddharoopa.data.model.Gender
import com.nascriptone.siddharoopa.ui.component.CurrentState
import com.nascriptone.siddharoopa.viewmodel.SiddharoopaViewModel
import kotlinx.coroutines.launch

@Composable
fun TableScreen(
    tableUIState: TableScreenState,
    entireSabdaList: List<EntireSabda>,
    viewModel: SiddharoopaViewModel,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {

    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)
    var isFetched by rememberSaveable { mutableStateOf(false) }
    val selectedSabda = tableUIState.selectedSabda
    val currentSabda =
        entireSabdaList.find { it.sabda == selectedSabda?.sabda && it.table == selectedSabda.table }
    if (currentSabda == null) return

    LaunchedEffect(Unit) {
        if (!isFetched) {
            viewModel.parseStringToDeclension(currentSabda)
            isFetched = true
        }
    }

    DisposableEffect(Unit) {

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                viewModel.resetTableState()
                isFetched = false
            }
        }

        lifecycleOwner.value.lifecycle.removeObserver(observer)

        onDispose {
            lifecycleOwner.value.lifecycle.addObserver(observer)
        }

    }


    when (val result = tableUIState.result) {
        is StringParse.Loading -> CurrentState(
            modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }

        is StringParse.Error -> CurrentState {
            Text(result.msg)
        }

        is StringParse.Success -> {
            TableScreenContent(
                result.declension,
                currentSabda,
                viewModel,
                snackbarHostState
            )
        }
    }
}


@Composable
fun TableScreenContent(
    declension: Declension,
    currentSabda: EntireSabda,
    viewModel: SiddharoopaViewModel,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {


    val scope = rememberCoroutineScope()

    val sabda = currentSabda.sabda
    val gender = Gender.valueOf(sabda.gender.uppercase())
    val genderSkt = stringResource(gender.sktName)
    val sabdaSkt = stringResource(R.string.sabda)
    val addFavMsg = stringResource(R.string.add_favorite_msg)
    val removeFavMsg = stringResource(R.string.remove_favorite_msg)
    val displayText = "${sabda.anta} $genderSkt \"${sabda.word}\" $sabdaSkt"

    val isItFavorite = currentSabda.isFavorite.status
    val snackBarMas = if (isItFavorite) removeFavMsg else addFavMsg


    Surface {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                displayText,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )
            DeclensionTable(declension)
            FavoriteView(
                isItFavorite = isItFavorite, onClick = {
                    scope.launch {
                        viewModel.toggleFavoriteSabda(currentSabda)
                        snackbarHostState.showSnackbar(snackBarMas)
                    }
                })
            Spacer(Modifier.height(28.dp))
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
                    DeclensionCell(cellValue, isPredefined = true, modifier = Modifier.weight(1F))
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
                    DeclensionCell(value = cellValue, modifier = Modifier.weight(1F))
                    if (i != formEntries.lastIndex) VerticalDivider()
                }
            }
            if (i != caseEntries.lastIndex) HorizontalDivider()
        }
    }
}


@Composable
fun FavoriteView(
    isItFavorite: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick) {
                AnimatedVisibility(!isItFavorite) {
                    Icon(
                        Icons.Rounded.FavoriteBorder, null
                    )
                }
                AnimatedVisibility(isItFavorite) {
                    Icon(
                        Icons.Rounded.Favorite, null, tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Text("Add to Favorites")
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


