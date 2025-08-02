package com.nascriptone.siddharoopa.ui.screen.home

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.data.model.Sound
import com.nascriptone.siddharoopa.data.model.Table
import com.nascriptone.siddharoopa.ui.component.CurrentState
import com.nascriptone.siddharoopa.ui.screen.SiddharoopaRoutes
import com.nascriptone.siddharoopa.viewmodel.SiddharoopaViewModel

@Composable
fun HomeScreen(
    viewModel: SiddharoopaViewModel,
    navHostController: NavHostController,
    homeScreenState: HomeScreenState,
    modifier: Modifier = Modifier
) {
    when (val result = homeScreenState.result) {
        is ObserveSabda.Loading -> CurrentState {
            CircularProgressIndicator()
        }

        is ObserveSabda.Error -> CurrentState {
            Text(result.msg)
        }

        is ObserveSabda.Success -> HomeScreenContent(
            viewModel = viewModel,
            navHostController = navHostController
        )
    }
}

@Composable
fun HomeScreenContent(
    viewModel: SiddharoopaViewModel,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {

    val tableViews = remember {
        listOf<TableView>(
            TableView(
                table = Table.GENERAL,
                option = OptionView(
                    sound = Sound.entries,
                    displayWord = DisplayWord(
                        vowelResId = R.string.general_vowel,
                        consonantResId = R.string.general_consonant
                    )
                )
            ),
            TableView(
                table = Table.SPECIFIC,
                option = OptionView(
                    sound = Sound.entries,
                    displayWord = DisplayWord(
                        vowelResId = R.string.specific_vowel,
                        consonantResId = R.string.specific_consonant
                    )
                )
            )
        )
    }

    val scrollState = rememberScrollState()


    Surface {
        Column(
            modifier = modifier.verticalScroll(scrollState)
        ) {
            tableViews.forEach { tables ->
                val tableName = when (tables.table) {
                    Table.GENERAL -> stringResource(R.string.general_table)
                    Table.SPECIFIC -> stringResource(R.string.specific_table)
                }
                View(tableName) {
                    val option = tables.option
                    option.sound.forEach { sound ->
                        val title = stringResource(sound.skt)
                        val displayWordResId = when (sound) {
                            Sound.VOWELS -> option.displayWord.vowelResId
                            Sound.CONSONANTS -> option.displayWord.consonantResId
                        }
                        val displayWord = stringResource(displayWordResId)
                        Option(
                            title = title,
                            displayWord = displayWord,
                            onCardClick = {
                                viewModel.updateTable(tables.table, sound)
                                navHostController.navigate(SiddharoopaRoutes.Category.name)
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}


@Composable
fun View(
    title: String, modifier: Modifier = Modifier, content: @Composable (ColumnScope.() -> Unit)
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(Modifier.height(8.dp))
        content()
    }
}

@Composable
fun Option(
    title: String, displayWord: String, onCardClick: () -> Unit, modifier: Modifier = Modifier
) {
    Card(
        onClick = onCardClick, modifier = modifier
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title, style = MaterialTheme.typography.headlineMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = displayWord,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = onCardClick, modifier = Modifier.align(Alignment.End)
            ) {
                Text(stringResource(R.string.see_all))
                Spacer(Modifier.width(12.dp))
                Icon(Icons.AutoMirrored.Rounded.ArrowForward, null)
            }
        }
    }
}

data class TableView(
    val table: Table,
    val option: OptionView
)

data class OptionView(
    val sound: List<Sound>,
    val displayWord: DisplayWord
)

data class DisplayWord(
    @StringRes val vowelResId: Int,
    @StringRes val consonantResId: Int
)