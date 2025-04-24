package com.nascriptone.siddharoopa.ui.screen.quiz

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun QuizHomeScreen(modifier: Modifier = Modifier) {
    Surface {
        Column(modifier) {
            Text("Hello this is quiz home screen!")
            Text("Welcome to Quiz section")
        }
    }
}