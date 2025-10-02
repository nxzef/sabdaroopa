package com.nascriptone.siddharoopa.ui.screen.quiz

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QuizReviewScreen(
    uiState: QuizSectionState,
    modifier: Modifier = Modifier
) {
    val list = uiState.creationState.requireSuccess { it.isNotEmpty() }
    Surface {
        LazyColumn(modifier = modifier) {
            item {
                Spacer(Modifier.height(20.dp))
            }
            items(list) { item ->
                QuestionAnswerReview(
                    item = item,
                    lastIndex = list.lastIndex,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            item {
                Spacer(Modifier.height(36.dp))
            }
        }
    }
}