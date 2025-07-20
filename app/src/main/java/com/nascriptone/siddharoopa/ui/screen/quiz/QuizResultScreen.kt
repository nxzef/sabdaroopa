package com.nascriptone.siddharoopa.ui.screen.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nascriptone.siddharoopa.ui.theme.SiddharoopaTheme

@Composable
fun QuizResultScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    Surface {
        Column(
            modifier =
                modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Congratulation!!",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 24.dp)
            )
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = MaterialTheme.shapes.extraLarge
                    )
                    .fillMaxWidth()
                    .heightIn(200.dp, 280.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "70%",
                        style = MaterialTheme.typography.displayLarge
                    )
                    Text(
                        text = "Score",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
            Column {
                Text("Statistics", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = MaterialTheme.shapes.extraLarge
                        )
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    repeat(4) {
                        Text("Action $it")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun QuizResultScreenPreview() {
    SiddharoopaTheme(true) {
        QuizResultScreen(
            navHostController = rememberNavController()
        )
    }
}