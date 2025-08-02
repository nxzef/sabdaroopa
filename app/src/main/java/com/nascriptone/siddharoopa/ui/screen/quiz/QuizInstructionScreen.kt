package com.nascriptone.siddharoopa.ui.screen.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nascriptone.siddharoopa.ui.theme.SiddharoopaTheme


@Composable
fun QuizInstructionScreen(
    modifier: Modifier = Modifier
) {
    Surface(modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainer),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column {
                Spacer(Modifier.height(20.dp))
                Text("Hello 1")
            }

            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,

                ) {
                Text("Middle")
            }

            Column {
                Text("hello 2")
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CustomRowPreview() {
    SiddharoopaTheme(true) {
        QuizInstructionScreen()
    }
}
