package com.nascriptone.siddharoopa.ui.screen.quiz

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nascriptone.siddharoopa.ui.component.CurrentState

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun QuizQuestionScreen(
    quizSectionState: QuizSectionState,
    modifier: Modifier = Modifier
) {

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

                is CreationState.Success -> Column {
                    result.data.forEach { each ->
                        Text(each.sabda.word)
                        Text(each.sabda.id.toString())
                        Text(each.sabda.gender)
                    }
                }
            }
        }
    }
}


//@Preview
//@Composable
//fun QuizQuestionScreenPreview() {
//    SiddharoopaTheme {
//        QuizQuestionScreen(
//            modifier = Modifier.fillMaxSize()
//        )
//    }
//}