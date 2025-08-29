package com.nascriptone.siddharoopa.ui.screen.category

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.data.model.Category
import com.nascriptone.siddharoopa.data.model.Gender
import com.nascriptone.siddharoopa.data.model.Sound
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.ui.component.CurrentState
import com.nascriptone.siddharoopa.ui.component.getSupportingText
import kotlinx.coroutines.delay

@Composable
fun CategoryScreen(
    initialCategory: Category,
    initialSound: Sound,
    onSabdaClick: (Int) -> Unit,
    categoryViewModel: CategoryViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by categoryViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        val filter = Filter(
            selectedCategory = initialCategory,
            selectedSound = initialSound
        )
        categoryViewModel.initializeFilter(filter)
    }

    Surface {
        Column(modifier) {
            SoundTab(
                selectedSound = uiState.filter.selectedSound,
                onSoundChange = categoryViewModel::updateSoundFilter
            )
            GenderFilterChipRow(
                selectedGender = uiState.filter.selectedGender,
                onGenderChange = categoryViewModel::updateGenderFilter
            )
            when (val filterState = uiState.filterState) {
                is FilterState.Success -> CategoryScreenList(
                    filteredData = filterState.data,
                    onClick = onSabdaClick
                )

                is FilterState.Loading -> CurrentState {
                    CircularProgressIndicator()
                }

                is FilterState.Empty -> CurrentState {
                    Text("Empty...")
                }
            }
        }
    }
}

@Composable
fun SoundTab(
    selectedSound: Sound?,
    onSoundChange: (Sound) -> Unit,
    modifier: Modifier = Modifier
) {
    if (selectedSound == null) return
    SecondaryTabRow(
        selectedTabIndex = selectedSound.ordinal,
        modifier = modifier
    ) {
        Sound.entries.forEach { sound ->
            Tab(
                selected = sound == selectedSound,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = { onSoundChange(sound) },
            ) {
                Text(
                    text = stringResource(sound.skt),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun GenderFilterChipRow(
    selectedGender: Gender?,
    onGenderChange: (Gender?) -> Unit,
    modifier: Modifier = Modifier
) {
    val genders = remember { setOf(null) + Gender.entries }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.width(16.dp))
        genders.forEach { gender ->
            val isSelected = selectedGender == gender
            val label = stringResource(gender?.skt ?: R.string.all_skt)
            FilterChip(
                selected = isSelected,
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Unspecified,
                        fontWeight = FontWeight.W600
                    )
                },
                onClick = { onGenderChange(gender) },
                leadingIcon = {
                    AnimatedVisibility(isSelected) {
                        Icon(Icons.Rounded.Check, null)
                    }
                },
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp)
            )
        }
        Spacer(Modifier.width(16.dp))
    }
}

@Composable
fun CategoryScreenList(
    filteredData: List<Sabda>,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var isReady by rememberSaveable { mutableStateOf(false) }
    val defaultDurationMillis = AnimationConstants.DefaultDurationMillis
    LaunchedEffect(Unit) {
        delay(defaultDurationMillis.toLong())
        isReady = true
    }
    AnimatedContent(
        targetState = isReady
    ) { ready ->
        if (ready) LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            state = rememberLazyListState(),
            modifier = modifier.padding(horizontal = 12.dp)
        ) {
            itemsIndexed(filteredData, key = { i, _ -> i }) { _, sabda ->
                SabdaItem(
                    sabda = sabda,
                    onClick = onClick
                )
            }
            item {
                Spacer(Modifier.height(52.dp))
            }
        }
        else CurrentState {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun SabdaItem(
    sabda: Sabda,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.large
            )
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = { onClick(sabda.id) })
            .padding(12.dp)

    ) {
        Text(
            text = sabda.word,
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = sabda.translit)
            Spacer(Modifier.width(12.dp))
            Text(
                text = "(${sabda.meaning})",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = getSupportingText(sabda),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .7f)
            )
            if (sabda.isFavorite) {
                Icon(
                    imageVector = Icons.Rounded.Favorite,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surfaceTint,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}