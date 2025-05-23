package com.nascriptone.siddharoopa.ui.screen.quiz

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nascriptone.siddharoopa.ui.screen.SiddharoopaRoutes
import com.nascriptone.siddharoopa.ui.theme.SiddharoopaTheme

@Composable
fun QuizResultScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    Surface {
        Column(
            modifier = modifier
        ) {
            Text("Hello World This is Result Screen")
            Text("Your result is 100%")
            TextButton(
                onClick = {
                    navHostController.navigate(SiddharoopaRoutes.QuizReview.name)
                }
            ) {
                Text("See Review")
            }
        }
    }
}

@Preview
@Composable
fun QuizResultScreenPreview() {
    SiddharoopaTheme(false) {
        QuizResultScreen(
            modifier = Modifier.fillMaxSize(),
            navHostController = rememberNavController()
        )
    }
}