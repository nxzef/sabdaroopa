package com.nascriptone.siddharoopa.ui.screen.category

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.data.model.EntireSabda
import com.nascriptone.siddharoopa.data.model.Gender
import com.nascriptone.siddharoopa.data.model.Sound
import com.nascriptone.siddharoopa.ui.component.CurrentState
import com.nascriptone.siddharoopa.ui.screen.Routes
import com.nascriptone.siddharoopa.viewmodel.SiddharoopaViewModel

@Composable
fun CategoryScreen(
    viewModel: SiddharoopaViewModel,
    navHostController: NavHostController,
    entireSabdaList: List<EntireSabda>,
    categoryScreenState: CategoryScreenState,
    modifier: Modifier = Modifier
) {

    val currentSound = categoryScreenState.selectedSound
    val currentGender = categoryScreenState.selectedGender
    val tabItems = Sound.entries
    val genderSuggestions: Set<Gender?> = setOf(
        null, *Gender.entries.toTypedArray()
    )

    LaunchedEffect(Unit) {
        viewModel.filterSabda(entireSabdaList)
    }

    Surface {
        Column(modifier) {
            SecondaryTabRow(
                selectedTabIndex = tabItems.indexOf(currentSound)
            ) {
                tabItems.forEach { sound ->
                    val label = stringResource(sound.skt)
                    Tab(
                        selected = sound == currentSound, onClick = {
                            viewModel.updateSoundFilter(sound)
                        }, text = {
                            Text(label, style = MaterialTheme.typography.titleLarge)
                        }, unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.width(16.dp))
                genderSuggestions.forEach { gender ->
                    val selected = currentGender == gender
                    val label = stringResource(gender?.sktName ?: R.string.all_skt)
                    FilterChip(
                        selected = selected, label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.W600
                            )
                        }, onClick = {
                            viewModel.updateGenderFilter(gender)
                        }, leadingIcon = {
                            AnimatedVisibility(selected) {
                                Icon(Icons.Rounded.Check, null)
                            }
                        }, modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp)
                    )
                }
                Spacer(Modifier.width(16.dp))
            }
            when (val result = categoryScreenState.result) {
                is FilterState.Loading -> {
                    CurrentState(modifier = Modifier.weight(1f)) {
                        CircularProgressIndicator()
                    }
                }

                is FilterState.Error -> {
                    CurrentState(modifier = Modifier.weight(1f)) {
                        Text(result.msg)
                        Log.e("room_error", result.msg)
                    }
                }

                is FilterState.Success -> {
                    CategoryScreenList(
                        data = result.filteredData,
                        onClick = { details, sabda ->
                            viewModel.updateSelectedSabda(sabda)
                            navHostController.navigate(Routes.Table.name) {
                                launchSingleTop = true
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryScreenList(
    data: List<EntireSabda>,
    onClick: (String, EntireSabda) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier) {
        items(data) { sabda ->
            SabdaItem(
                entireSabda = sabda,
                onClick = onClick
            )
        }
        item {
            Spacer(Modifier.height(52.dp))
        }
    }
}

@Composable
fun SabdaItem(
    entireSabda: EntireSabda,
    onClick: (String, EntireSabda) -> Unit,
    modifier: Modifier = Modifier
) {

    val sabda = entireSabda.sabda
    val gender = Gender.valueOf(sabda.gender.uppercase())
    val genderInSkt = stringResource(gender.sktName)
    val sabdaInSkt = stringResource(R.string.sabda)
    val supportingText = "${sabda.anta} $genderInSkt \"${sabda.word}\" $sabdaInSkt"

    ListItem(
        headlineContent = {
            Text(sabda.word, style = MaterialTheme.typography.headlineSmall)
        }, supportingContent = {
            Text(
                supportingText,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .7F)
            )
        }, trailingContent = {
            if (entireSabda.isFavorite.status) {
                Icon(
                    Icons.Rounded.Favorite,
                    null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(12.dp)
                )
            }
        }, modifier = modifier.clickable(onClick = { onClick(supportingText, entireSabda) })
    )
}