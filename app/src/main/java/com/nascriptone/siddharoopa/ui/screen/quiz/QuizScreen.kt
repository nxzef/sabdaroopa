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
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberRangeSliderState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.ui.theme.SiddharoopaTheme

@Composable
fun QuizHomeScreen(modifier: Modifier = Modifier) {
    val quizHomeTitle = stringResource(R.string.start_your_quiz)
    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                quizHomeTitle,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(vertical = 16.dp)
            )
            Spacer(Modifier.height(32.dp))
            QuizChooseOptionView(
                title = "Choose Category"
            ) {
                QuizChooseOption(
                    optionName = stringResource(R.string.general_table)
                )
                QuizChooseOption(
                    optionName = stringResource(R.string.specific_table)
                )
            }
            QuizChooseOptionView(
                title = "Question Type"
            ) {
                QuizChooseOption(
                    optionName = ""
                )
                QuizChooseOption(
                    optionName = ""
                )
                QuizChooseOption(
                    optionName = ""
                )
            }
            QuizChooseOptionView(
                title = "Question Range"
            ) {
                QuestionRangeSlider()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionRangeSlider(
    modifier: Modifier = Modifier
) {

    val rangeSliderState = rememberRangeSliderState(
        0F,
        100F,
        valueRange = 1F..100F,
        onValueChangeFinished = {})

    RangeSlider(state = rangeSliderState)
}

@Composable
fun QuizChooseOptionView(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Column(modifier = modifier.padding(vertical = 24.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable
fun QuizChooseOption(
    optionName: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.padding(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(0.95F)
                .padding(12.dp),
        ) {
            RadioButton(
                selected = false,
                onClick = {},
            )
            Spacer(Modifier.width(8.dp))
            Text(
                optionName,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}


@Preview
@Composable
fun QuizHomeScreenContentPreview() {
    SiddharoopaTheme(true) {
        QuizHomeScreen(
            modifier = Modifier.fillMaxSize()
        )
    }
}