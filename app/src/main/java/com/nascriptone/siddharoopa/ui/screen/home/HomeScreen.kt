package com.nascriptone.siddharoopa.ui.screen.home

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.data.model.uiobj.CategoryOptionType
import com.nascriptone.siddharoopa.data.model.uiobj.CategoryViewType
import com.nascriptone.siddharoopa.data.model.uiobj.Sound
import com.nascriptone.siddharoopa.ui.screen.SiddharoopaRoutes
import com.nascriptone.siddharoopa.ui.screen.TableCategory
import com.nascriptone.siddharoopa.ui.theme.SiddharoopaTheme
import com.nascriptone.siddharoopa.viewmodel.SiddharoopaViewModel

@Composable
fun HomeScreen(
    viewModel: SiddharoopaViewModel,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {

    val vowSound = Sound(
        eng = stringResource(R.string.vowel_eng),
        skt = stringResource(R.string.vowel_skt)
    )
    val conSound = Sound(
        eng = stringResource(R.string.consonant_eng),
        skt = stringResource(R.string.consonant_skt)
    )

    val categoryViewList: List<CategoryViewType> = listOf(
        CategoryViewType(
            title = stringResource(R.string.general_category),
            category = TableCategory.General,
            options = listOf(
                CategoryOptionType(
                    sound = vowSound,
                    displayWord = stringResource(R.string.general_vowel)
                ),
                CategoryOptionType(
                    sound = conSound,
                    displayWord = stringResource(R.string.general_consonant)
                )
            )
        ),
        CategoryViewType(
            title = stringResource(R.string.specific_category),
            category = TableCategory.Specific,
            options = listOf(
                CategoryOptionType(
                    sound = vowSound,
                    displayWord = stringResource(R.string.specific_vowel)
                ),
                CategoryOptionType(
                    sound = conSound,
                    displayWord = stringResource(R.string.specific_consonant)
                )
            )
        )
    )


    LaunchedEffect(Unit) {
        viewModel.resetCategoryState()
    }

    Surface {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            categoryViewList.forEach { view ->
                CategoryView(
                    title = view.title
                ) {
                    view.options.forEach { option ->
                        CategoryOption(
                            title = option.sound.skt,
                            displayWord = option.displayWord,
                            onCardClick = {
                                viewModel.updateSelectedCategory(view, option.sound)
                                navHostController.navigate(SiddharoopaRoutes.Category.name) {
                                    launchSingleTop = true
                                }
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
fun CategoryView(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(Modifier.height(4.dp))
        content()
    }
}

@Composable
fun CategoryOption(
    title: String,
    displayWord: String,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onCardClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium
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
                onClick = onCardClick,
                modifier = Modifier
                    .align(Alignment.End)
            ) {
                Text(stringResource(R.string.see_all))
                Spacer(Modifier.width(12.dp))
                Icon(Icons.AutoMirrored.Rounded.ArrowForward, null)
            }
        }
    }
}


@Preview
@Composable
fun HomeScreenPreview() {
    Surface {
        SiddharoopaTheme {
            HomeScreen(viewModel = hiltViewModel(), rememberNavController())
        }
    }
}