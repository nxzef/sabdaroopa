package com.nascriptone.siddharoopa.ui.screen.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nascriptone.siddharoopa.ui.component.CurrentState

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun QuizQuestionScreen(
    quizSectionState: QuizSectionState,
    modifier: Modifier = Modifier
) {

    var questionIndex by rememberSaveable { mutableIntStateOf(0) }

    val scrollState = rememberScrollState()
    Surface {
        Column(
            modifier = modifier
                .verticalScroll(scrollState)
        ) {
            when (val result = quizSectionState.result) {
                is CreationState.Loading -> CurrentState {
                    CircularProgressIndicator()
                }

                is CreationState.Error -> CurrentState {
                    Text(result.msg)
                }

                is CreationState.Success -> CurrentState {
                    result.data.forEachIndexed { index, each ->
                        AnimatedVisibility(
                            index == questionIndex,
                            enter = slideInHorizontally(
                                initialOffsetX = { it / 2 }
                            ) + fadeIn(),
                            exit = slideOutHorizontally() + fadeOut()
                        ) {
                            val text = stringResource(each.question)
                            Row {
                                Text("Q ${index + 1}. ")
                                Text(text)
                            }
                        }
                    }
                    Button(onClick = {
                        if (questionIndex < result.data.lastIndex) {
                            questionIndex++
                        } else {
                            questionIndex = 0
                        }
                    }) { Text("Increase") }
                }
            }
        }
    }
}

