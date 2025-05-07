package com.nascriptone.siddharoopa.ui.screen.quiz

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
                                    val stringRes = stringResource(e.question)
                                    val text =
                                        replacePlaceholders(stringRes, state.data.questionKey)
                                    Text(text)
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

//private fun replacePlaceholders(template: String, values: Map<String, String>): String {
//    return Regex("""\{(\w+)}""").replace(template) { matchResult ->
//        val key = matchResult.groupValues[1]
//        values[key] ?: matchResult.value
//    }
//}

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


//CurrentState {
//    result.data.forEachIndexed { index, each ->
//        AnimatedVisibility(
//            index == questionIndex,
//            enter = slideInHorizontally(
//                initialOffsetX = { it / 2 }
//            ) + fadeIn(),
//            exit = slideOutHorizontally() + fadeOut()
//        ) {
//
//            when (val option = each.option) {
//                is Option.McqOption -> {
//                    val fromString = stringResource(each.question)
//                    val text =
//                        replacePlaceholders(fromString, option.data.questionKey)
//                    Column {
//                        Row {
//                            Text("Q ${index + 1}. ")
//                            Text(text)
//                        }
//                    }
//                }
//
//                is Option.MtfOption -> {}
//            }
//        }
//    }
//    Button(onClick = {
//        if (questionIndex < result.data.lastIndex) {
//            questionIndex++
//        } else {
//            questionIndex = 0
//        }
//    }) { Text("Increase") }
//}

