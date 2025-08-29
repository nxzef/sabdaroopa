package com.nascriptone.siddharoopa.ui.screen.home

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.data.model.Category
import com.nascriptone.siddharoopa.data.model.Sound

@Composable
fun HomeScreen(
    onCardClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    HomeScreenContent(
        onCardClick = onCardClick,
        modifier = modifier
    )
}

@Composable
fun HomeScreenContent(
    onCardClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {

    val categoryViews = remember {
        listOf(
            TableView(
                category = Category.GENERAL,
                option = OptionView(
                    sound = Sound.entries,
                    displayWord = DisplayWord(
                        vowelResId = R.string.general_vowel,
                        consonantResId = R.string.general_consonant
                    )
                )
            ),
            TableView(
                category = Category.SPECIFIC,
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

    Surface {
        Column(
            modifier =
                modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(24.dp))
            categoryViews.forEach { tables ->
                val categoryName = stringResource(tables.category.eng)
                val subTitle = stringResource(tables.category.skt)
                View(
                    title = categoryName,
                    sunTitle = subTitle
                ) {
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
                            displayWord = "$displayWord...",
                            onClick = {
                                onCardClick(tables.category.ordinal, sound.ordinal)
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}


@Composable
fun View(
    title: String,
    sunTitle: String,
    modifier: Modifier = Modifier,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(vertical = 20.dp)
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = sunTitle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(Modifier.height(8.dp))
        content()
    }
}

@Composable
fun Option(
    title: String,
    displayWord: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(vertical = 8.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.large
            )
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
            .padding(16.dp)

    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(8.dp),
        ) {
            Text(
                text = displayWord,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(0.7f)
            )
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = "ChevronRight",
            )
        }
    }
}

data class TableView(
    val category: Category, val option: OptionView
)

data class OptionView(
    val sound: List<Sound>, val displayWord: DisplayWord
)

data class DisplayWord(
    @StringRes val vowelResId: Int, @StringRes val consonantResId: Int
)