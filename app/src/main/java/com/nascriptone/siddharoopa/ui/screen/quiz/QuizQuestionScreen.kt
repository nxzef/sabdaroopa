package com.nascriptone.siddharoopa.ui.screen.quiz

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nascriptone.siddharoopa.ui.component.CurrentState
import com.nascriptone.siddharoopa.ui.theme.SiddharoopaTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun QuizQuestionScreen(
    quizSectionState: QuizSectionState,
    modifier: Modifier = Modifier
) {
    when (val result = quizSectionState.result) {
        is CreationState.Loading -> CurrentState {
            CircularProgressIndicator()
        }

        is CreationState.Error -> CurrentState {
            Text(result.msg)
        }

        is CreationState.Success -> QuizQuestionScreenContent(
            result.data,
            quizSectionState,
            modifier = modifier
        )
    }
}

@Composable
fun QuizQuestionScreenContent(
    data: List<QuestionOption>,
    quizSectionState: QuizSectionState,
    modifier: Modifier = Modifier
) {


    var questionIndex by rememberSaveable { mutableIntStateOf(0) }
    val questionCount = quizSectionState.questionRange.toInt()

    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
        ) {
            Spacer(Modifier.height(24.dp))
            Text(
                "${questionIndex + 1} / $questionCount",
                style = MaterialTheme.typography.titleLarge
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F),
                contentAlignment = Alignment.Center
            ) {
                data.forEachIndexed { i, e ->
                    Column {
                        AnimatedVisibility(
                            i == questionIndex,
                            enter = slideInHorizontally(
                                initialOffsetX = { it / 2 }
                            ) + fadeIn(),
                            exit = slideOutHorizontally() + fadeOut()
                        ) {

                            when (val state = e.option) {
                                is Option.McqOption -> {
                                    RegexText(e.question, state.data.questionKey)
                                }

                                is Option.MtfOption -> {

                                }
                            }

                        }
                    }
                }
            }
            Button(onClick = {
                if (questionIndex < (questionCount - 1)) {
                    questionIndex++
                } else {
                    questionIndex = 0
                }
            }) {
                Text("NEXT")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}


@Composable
fun RegexText(
    @StringRes template: Int,
    key: Map<String, String>,
    modifier: Modifier = Modifier
) {
    val stringRes = stringResource(template)
    val text =
        replacePlaceholders(stringRes, key)
    Text(text, modifier = modifier)
}


private fun replacePlaceholders(template: String, values: Map<String, String>): String {
    return template.replace(Regex("\\{(\\w+)\\}")) { match ->
        values[match.groupValues[1]] ?: match.value
    }
}


//values.entries.fold(template) { acc, entry ->
//    acc.replace("{${entry.key}}", entry.value)
//}


@Preview
@Composable
fun QuizQuestionScreenPreview() {
    SiddharoopaTheme {
        QuizQuestionScreen(
            QuizSectionState()
        )
    }
}

