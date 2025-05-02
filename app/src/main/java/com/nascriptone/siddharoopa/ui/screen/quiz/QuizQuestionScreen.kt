package com.nascriptone.siddharoopa.ui.screen.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nascriptone.siddharoopa.ui.theme.SiddharoopaTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun QuizQuestionScreen(modifier: Modifier = Modifier) {
    val count = rememberSaveable { mutableIntStateOf(0) }
    Surface {
        Box(
            modifier,
            contentAlignment = Alignment.Center
        ) {
            repeat(10) {
                AnimatedVisibility(
                    it == count.intValue,
                    enter = slideInHorizontally(
                        initialOffsetX = { it / 2 }
                    ) + fadeIn(),
                    exit = slideOutHorizontally() + fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .size(360.dp),
//                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedVisibility(it == count.intValue) {
                            Text("Hello World $it", textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
        Button(onClick = {
            if (count.intValue > 8) {
                count.intValue = 0
            } else {
                count.intValue++
            }
        }) {
            Text("++")
        }
    }
}


@Preview
@Composable
fun QuizQuestionScreenPreview() {
    SiddharoopaTheme {
        QuizQuestionScreen(
            modifier = Modifier.fillMaxSize()
        )
    }
}