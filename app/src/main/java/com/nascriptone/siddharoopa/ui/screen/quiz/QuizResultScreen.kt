package com.nascriptone.siddharoopa.ui.screen.quiz

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.ui.theme.SiddharoopaTheme

@Composable
fun QuizResultScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {

    val screenTitle = stringResource(R.string.quiz_result_title)

    Surface {
        Column(
            modifier =
                modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
        ) {
            Text(
                text = screenTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 26.dp)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
//                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .fillMaxWidth()
                    .padding(8.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { 0.64f },
                        strokeWidth = 8.dp,
                        modifier = Modifier.size(110.dp)
                    )
                    Text(
                        text = "74%",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Text(
                    text = "Accuracy",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(4.dp)
                )
                Spacer(Modifier.height(12.dp))
                MessageText(
                    message = stringResource(R.string.full_1)
                )
                Spacer(Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = MaterialTheme.shapes.large
                        )
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Total Score",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "All Categories",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceBright,
                                    shape = MaterialTheme.shapes.large
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "18 / 30",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            MultipleChoiceQuestion()
            Spacer(Modifier.height(16.dp))
            MatchTheFollowing()
        }
    }
}

@Composable
fun MultipleChoiceQuestion(
    modifier: Modifier = Modifier
) {
    ModeView(
        title = "Multiple Choice (MCQ)",
        modifier = modifier
    ) {
        Text("Questions")
        HorizontalDivider()
        Text("Attended")
        HorizontalDivider()
        Text("Skipped")
        HorizontalDivider()
        Text("Correct")
        HorizontalDivider()
        Text("Wrong")
        HorizontalDivider()
        Text("Earned")
    }
}

@Composable
fun MatchTheFollowing(
    modifier: Modifier = Modifier
) {
    ModeView(
        title = "Match the Following (MTF)",
        modifier = modifier
    ) {
        Text("Sets")
        HorizontalDivider()
        Text("Pairs to Match")
        HorizontalDivider()
        Text("Attended Sets")
        HorizontalDivider()
        Text("Skipped Sets")
        HorizontalDivider()
        Text("Correct Matches")
        HorizontalDivider()
        Text("Earned")
    }
}

@Composable
fun ModeView(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Column(
        modifier = Modifier
            .then(modifier)
            .padding(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = MaterialTheme.shapes.large
                )
                .padding(12.dp)
        ) { content() }
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun MessageText(
    message: String,
    modifier: Modifier = Modifier
) {

    val config = LocalConfiguration.current
    val maxWidth = (0.75f * config.screenWidthDp).dp

    Text(
        text = message,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onTertiaryContainer,
        textAlign = TextAlign.Center,
        modifier = modifier
            .widthIn(max = maxWidth)
            .background(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = MaterialTheme.shapes.large
            )
            .padding(8.dp)
    )
}

@Composable
fun StatHighlightBox(
    label: String,
    result: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .height(100.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = MaterialTheme.shapes.large
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = result.toString(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

//@Preview
//@Composable
//fun MultipleChoiceQuestionPreview() {
//    SiddharoopaTheme {
//        MultipleChoiceQuestion()
//    }
//}

@Preview
@Composable
fun QuizResultScreenPreview() {
    SiddharoopaTheme(true) {
        QuizResultScreen(
            navHostController = rememberNavController()
        )
    }
}