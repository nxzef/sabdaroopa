package com.nascriptone.siddharoopa.ui.screen.quiz

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.ui.screen.SiddharoopaRoutes
import com.nascriptone.siddharoopa.ui.theme.SiddharoopaTheme

@Composable
fun QuizHomeScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .padding(horizontal = 8.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(Modifier.height(20.dp))
            QuizChooseOptionView(
                title = "Choose Category"
            ) {
                QuizChooseOption(
                    optionName = stringResource(R.string.general_table),
                    optionSubTitle = stringResource(R.string.general_subhead_eng)
                )
                HorizontalDivider()
                QuizChooseOption(
                    optionName = stringResource(R.string.specific_table),
                    optionSubTitle = stringResource(R.string.specific_subhead_eng)
                )
            }
            QuizChooseOptionView(
                title = "Question Type"
            ) {
                QuizChooseOption(
                    optionName = "Multiple Choice (MCQ)"
                )
                HorizontalDivider()
                QuizChooseOption(
                    optionName = "Fill in the Blanks"
                )
                HorizontalDivider()
                QuizChooseOption(
                    optionName = "Match the Following"
                )
            }
            QuizChooseOptionView(
                title = "Question Range"
            ) {
                StepSlider(
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(Modifier.height(28.dp))
            Button(
                onClick = {
                    navHostController.navigate(SiddharoopaRoutes.QuizQuestion.name)
                }, modifier = Modifier
                    .fillMaxWidth(0.9F)
            ) {
                Text("Begin Quiz")
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepSlider(
    modifier: Modifier = Modifier
) {

    val sliderState = rememberSliderState(
        value = 10F,
        steps = 4,
        valueRange = 5F..30F
    )

    Slider(
        state = sliderState,
        modifier = modifier
    )

}

@Composable
fun QuizChooseOptionView(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Column(
        modifier = modifier
            .padding(vertical = 20.dp)
            .fillMaxWidth(0.9F)
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(16.dp))
        OutlinedCard(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            )
        ) {
            content()
        }
    }
}

@Composable
fun QuizChooseOption(
    optionName: String,
    modifier: Modifier = Modifier,
    optionSubTitle: String? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 8.dp),
    ) {
        RadioButton(
            selected = false,
            onClick = {},
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(
                optionName,
                style = MaterialTheme.typography.titleMedium,
            )
            if (optionSubTitle != null) {
                Text(
                    optionSubTitle,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.7F)
                )
            }
        }
    }
}


@Preview
@Composable
fun QuizHomeScreenContentPreview() {
    SiddharoopaTheme(true) {
        QuizHomeScreen(
            navHostController = rememberNavController(),
            modifier = Modifier.fillMaxSize()
        )
    }
}