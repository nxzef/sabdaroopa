package com.nascriptone.siddharoopa.ui.screen.category

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.data.model.uiobj.Sound
import com.nascriptone.siddharoopa.data.model.uiobj.Suggestion
import com.nascriptone.siddharoopa.ui.component.CurrentState
import com.nascriptone.siddharoopa.ui.screen.Gender
import com.nascriptone.siddharoopa.ui.screen.SiddharoopaRoutes
import com.nascriptone.siddharoopa.viewmodel.SiddharoopaViewModel

@Composable
fun CategoryScreen(
    viewModel: SiddharoopaViewModel,
    navHostController: NavHostController,
    categoryScreenState: CategoryScreenState,
    modifier: Modifier = Modifier
) {

    LaunchedEffect(Unit) {
        viewModel.fetchSabda()
    }


    when (val result = categoryScreenState.result) {
        is DataFetchState.Loading -> {
            CurrentState {
                CircularProgressIndicator()
            }
        }

        is DataFetchState.Error -> {
            CurrentState {
                Text(result.msg)
            }
        }

        is DataFetchState.Success -> {
            CategoryScreenContent(
                data = categoryScreenState.filteredData,
                currentSound = categoryScreenState.selectedSound,
                currentGender = categoryScreenState.selectedGender,
                viewModel = viewModel,
                navHostController = navHostController,
                modifier = modifier
            )
        }
    }
}

@Composable
fun CategoryScreenContent(
    data: List<Sabda>,
    currentSound: Sound?,
    currentGender: Gender?,
    viewModel: SiddharoopaViewModel,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {


    val tabItems: List<Sound> = listOf(
        Sound(
            eng = stringResource(R.string.vowel_eng),
            skt = stringResource(R.string.vowel_skt)
        ),
        Sound(
            eng = stringResource(R.string.consonant_eng),
            skt = stringResource(R.string.consonant_skt)
        )
    )

    val genderSuggestions: Set<Suggestion> = remember {
        setOf(
            Suggestion(
                skt = R.string.all_skt
            ),
            Suggestion(
                gender = Gender.Masculine,
                skt = R.string.masculine_skt
            ),
            Suggestion(
                gender = Gender.Feminine,
                skt = R.string.feminine_skt
            ),
            Suggestion(
                gender = Gender.Neuter,
                skt = R.string.neuter_skt
            )
        )
    }

    Surface {
        Column(
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            TabRow(
                selectedTabIndex = tabItems.indexOf(currentSound),
            ) {
                tabItems.forEach { item ->
                    Tab(
                        selected = currentSound?.eng == item.eng,
                        onClick = {
                            viewModel.updateSoundFilter(item)
                        },
                        text = {
                            Text(item.skt, style = MaterialTheme.typography.titleLarge)
                        },
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
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
                Spacer(Modifier.width(24.dp))
                genderSuggestions.forEach { suggestion ->
                    val gender = suggestion.gender
                    val selected = currentGender == gender
                    FilterChip(
                        selected = selected,
                        label = {
                            Text(
                                text = stringResource(suggestion.skt),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.W600
                            )
                        },
                        onClick = {
                            viewModel.updateGenderFilter(gender)
                        },
                        leadingIcon = {
                            AnimatedVisibility(
                                visible = selected
                            ) {
                                Icon(Icons.Rounded.Check, null)
                            }
                        },
                        modifier = Modifier
                            .padding(horizontal = 6.dp, vertical = 8.dp)
                    )
                }
                Spacer(Modifier.width(24.dp))
            }

            LazyColumn(
                modifier = modifier
                    .weight(1F)
            ) {

                items(data) { sabda ->
                    SabdaItem(
                        sabda = sabda,
                        onClick = { details ->
                            viewModel.updateSelectedTable(sabda, details)
                            navHostController.navigate(SiddharoopaRoutes.Table.name)
                        }
                    )
                }

                item {
                    Spacer(Modifier.height(52.dp))
                }
            }

        }
    }

}

@Composable
fun SabdaItem(
    sabda: Sabda,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val genderInSkt = when (sabda.gender) {
        stringResource(R.string.masculine_eng).lowercase() -> stringResource(R.string.masculine_skt)
        stringResource(R.string.feminine_eng).lowercase() -> stringResource(R.string.feminine_skt)
        else -> stringResource(R.string.neuter_skt)
    }

    val sabdaInSkt = stringResource(R.string.sabda)
    val supportingText = "${sabda.anta} $genderInSkt ${sabda.word} $sabdaInSkt"

    ListItem(
        headlineContent = {
            Text(sabda.word, style = MaterialTheme.typography.headlineSmall)
        },
        supportingContent = {
            Text(
                supportingText,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6F)
            )
        },
        modifier = modifier
            .clickable(onClick = { onClick(supportingText) })
    )
}