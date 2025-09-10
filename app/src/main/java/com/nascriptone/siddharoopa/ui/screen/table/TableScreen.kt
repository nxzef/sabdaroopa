package com.nascriptone.siddharoopa.ui.screen.table

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.rounded.Quiz
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
import com.nascriptone.siddharoopa.core.utils.toPascalCase
import com.nascriptone.siddharoopa.data.model.Declension
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.ui.component.CurrentState
import com.nascriptone.siddharoopa.ui.component.CustomToolTip
import com.nascriptone.siddharoopa.ui.component.getSupportingText
import kotlinx.coroutines.launch

@Composable
fun TableScreen(
    onQuizClick: (Int) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    tableViewModel: TableViewModel = hiltViewModel()
) {
    val sabda by tableViewModel.sabda.collectAsStateWithLifecycle()
    sabda?.let { sabda ->
        TableScreenContent(
            sabda = sabda,
            onQuizClick = onQuizClick,
            tableViewModel = tableViewModel,
            snackbarHostState = snackbarHostState,
            modifier = modifier
        )
    } ?: CurrentState {
        Text("Could not find declension table.")
    }
}

@Composable
fun TableScreenContent(
    sabda: Sabda,
    onQuizClick: (Int) -> Unit,
    tableViewModel: TableViewModel,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {

    val scope = rememberCoroutineScope()
    val isItFavorite = sabda.isFavorite
    val favLabel = if (isItFavorite) stringResource(R.string.remove_favorite_msg)
    else stringResource(R.string.add_favorite_msg)
    val favMessage = if (isItFavorite) stringResource(R.string.removed_favorite_msg)
    else stringResource(R.string.added_favorite_msg)

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
            Column(
                modifier = modifier
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = MaterialTheme.shapes.large
                    )
                    .clip(MaterialTheme.shapes.large)
            ) {
                OptionView(
                    icon = {
                        AnimatedContent(targetState = isItFavorite) { fav ->
                            CustomToolTip("Favorite") {
                                if (fav) Icon(
                                    imageVector = Icons.Rounded.Favorite,
                                    tint = MaterialTheme.colorScheme.surfaceTint,
                                    contentDescription = null
                                ) else Icon(
                                    imageVector = Icons.Rounded.FavoriteBorder,
                                    contentDescription = null
                                )
                            }
                        }
                    },
                    label = {
                        AnimatedContent(targetState = favLabel) { label ->
                            Text(label)
                        }
                    },
                    onClick = {
                        scope.launch {
                            tableViewModel.toggleFavoriteSabda(sabda.id)
                            snackbarHostState.showSnackbar(favMessage)
                        }
                    }
                )
                HorizontalDivider()
                OptionView(
                    icon = {
                        CustomToolTip("Quiz") {
                            Icon(
                                imageVector = Icons.Rounded.Quiz,
                                contentDescription = null
                            )
                        }
                    },
                    label = { Text("Take Quiz") },
                    onClick = { onQuizClick(sabda.id) }
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Details",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(vertical = 16.dp)
            )
            DetailView(sabda)
            Spacer(Modifier.height((TopAppBarDefaults.TopAppBarExpandedHeight) * 2))
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
                    modifier = Modifier.weight(1f)
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
fun DetailView(
    sabda: Sabda,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.large
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        RowItem("Word", sabda.word)
        HorizontalDivider()
        RowItem("Meaning", sabda.meaning)
        HorizontalDivider()
        RowItem("Roman (IAST)", sabda.translit)
        HorizontalDivider()
        RowItem("End Sound", sabda.anta)
        HorizontalDivider()
        RowItem(
            "Category",
            "${stringResource(sabda.category.skt)}\t-\t${sabda.category.toPascalCase()}"
        )
        HorizontalDivider()
        RowItem("Gender", "${stringResource(sabda.gender.skt)}\t-\t${sabda.gender.toPascalCase()}")
        HorizontalDivider()
        RowItem("Sound", "${stringResource(sabda.sound.skt)}\t-\t${sabda.sound.toPascalCase()}")
    }
}

@Composable
fun RowItem(
    lead: String,
    tail: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(Modifier.weight(1f)) {
            Text(text = lead, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Box(Modifier.weight(1f)) {
            Text(text = tail, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun OptionView(
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick) { icon.invoke() }
        Spacer(Modifier.width(12.dp))
        label.invoke()
    }
}

