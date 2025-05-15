package com.nascriptone.siddharoopa.ui.screen.quiz

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import com.nascriptone.siddharoopa.data.model.uiobj.CaseName
import com.nascriptone.siddharoopa.data.model.uiobj.FormName
import com.nascriptone.siddharoopa.ui.component.CurrentState

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

                            Column {
                                when (val state = e.option) {
                                    is Option.McqOption -> {
                                        RegexText(e.question, state.data.questionKey)
                                        Spacer(Modifier.height(20.dp))
                                        state.data.options.forEachIndexed { i, option ->
                                            Text(
                                                option,
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                        }
                                    }

                                    is Option.MtfOption -> {
                                        RegexText(e.question, state.data.questionKey)
                                        Spacer(Modifier.height(20.dp))
                                        Row {
                                            Column {
                                                state.data.options.forEach { option ->
                                                    Text(option.key)
                                                }
                                            }
                                            Column {
                                                state.data.options.forEach { option ->
                                                    Text(option.value)
                                                }
                                            }
                                        }
                                    }
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
    val mutableKey = key.toMutableMap()
    val stringRes = stringResource(template)
    val vibaktiKey = "vibhakti"
    val vachanaKey = "vachana"
    if (mutableKey.containsKey(vibaktiKey) || mutableKey.containsKey(vachanaKey)) {
        val vibaktiValue = key.getValue(vibaktiKey)
        val vachanaValue = key.getValue(vachanaKey)
        val vibaktiEnum = CaseName.valueOf(vibaktiValue)
        val vachanaEnum = FormName.valueOf(vachanaValue)
        val vibaktiSktName = stringResource(vibaktiEnum.sktName)
        val vachanaSktName = stringResource(vachanaEnum.sktName)
        mutableKey[vibaktiKey] = vibaktiSktName
        mutableKey[vachanaKey] = vachanaSktName
    }
    val finalKey = mutableKey.toMap()
    val text =
        replacePlaceholders(stringRes, finalKey)
    Text(text, modifier = modifier, style = MaterialTheme.typography.headlineSmall)
}


private fun replacePlaceholders(template: String, values: Map<String, String>): String {
    return template.replace(Regex("\\{(\\w+)\\}")) { match ->
        values[match.groupValues[1]] ?: match.value
    }
}

