package com.nascriptone.siddharoopa.ui.screen.quiz

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.ui.component.CurrentState
import com.nascriptone.siddharoopa.viewmodel.SiddharoopaViewModel

@Composable
fun QuizResultScreen(
    viewModel: SiddharoopaViewModel,
    quizSectionState: QuizSectionState,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {

    val screenTitle = stringResource(R.string.quiz_result_title)

    var calculated by rememberSaveable { mutableStateOf(false) }



    LaunchedEffect(Unit) {
        if (!calculated) {
            viewModel.quizValuation()
            calculated = true
        }
    }

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
                    .padding(vertical = 20.dp)
            )

            when (val data = quizSectionState.result) {
                is ValuationState.Calculate -> CurrentState {
                    CircularProgressIndicator()
                }

                is ValuationState.Error -> CurrentState {
                    Text(data.message)
                }

                is ValuationState.Success -> {
                    MainDashboard(
                        dashboard = data.result.dashboard
                    )
                    Spacer(Modifier.height(12.dp))
                    MultipleChoiceQuestion()
                    MatchTheFollowing()
                    FinalSummary()
                    ReviewView()
                }
            }
        }
    }
}

@Composable
fun MainDashboard(
    dashboard: Dashboard,
    modifier: Modifier = Modifier
) {

    var progress by rememberSaveable { mutableFloatStateOf(0f) }

    val currentProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(2000),
    )

    LaunchedEffect(Unit) {
        progress = 0.46f
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { currentProgress },
                strokeWidth = 8.dp,
                modifier = Modifier.size(116.dp)
            )
            Text(
                text = "",
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
            message = stringResource(R.string.quarter_1)
        )
        Spacer(Modifier.height(24.dp))
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
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "All Categories",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.large
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = "${dashboard.score} / ${dashboard.totalScore}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
fun FinalSummary(
    modifier: Modifier = Modifier,
) {

    val style = MaterialTheme.typography.bodyMedium.copy(
        fontWeight = FontWeight.Medium
    )

    ModeView(
        title = "Final Summary",
        modifier = modifier
    ) {
        TextWithDivider("Total Questions", style = style)
        TextWithDivider("Total Possible Marks", style = style)
        TextWithDivider("Total Score", style = style)
        TextWithDivider("Accuracy", style = style, disableDivider = true)
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
        TextWithDivider("Number of Questions")
        TextWithDivider("Attended")
        TextWithDivider("Skipped")
        TextWithDivider("Correct Answers")
        TextWithDivider("Wrong Answers")
        TextWithDivider("MCQ Score", disableDivider = true)
    }
}

@Composable
fun MatchTheFollowing(
    modifier: Modifier = Modifier
) {
    ModeView(
        title = "Match the Following",
        modifier = modifier
    ) {
        TextWithDivider("Number of Sets")
        TextWithDivider("Pairs to Match")
        TextWithDivider("Attended Sets")
        TextWithDivider("Skipped Sets")
        TextWithDivider("Correct Matches")
        TextWithDivider("MTF Score", disableDivider = true)
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
        style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Medium
        ),
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
fun ReviewView(
    modifier: Modifier = Modifier
) {
    ModeView(
        title = "Quiz Review",
        disableBackgroundColor = false,
        modifier = modifier
    ) {
        repeat(5) {
            val questionNumber = it + 1
            Spacer(Modifier.height(if (it != 0) 12.dp else 4.dp))
            QuestionWithNumber(
                questionNumber,
                modifier = modifier
            ) {
                if (questionNumber <= 3) {
                    MultipleChoiceReview()
                } else {
                    MatchTheFollowingReview()
                }
            }
            Spacer(Modifier.height(12.dp))
            HorizontalDivider()
        }
    }
}

@Composable
fun MultipleChoiceReview(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .then(modifier)
            .border(
                border = BorderStroke(
                    width = DividerDefaults.Thickness,
                    color = DividerDefaults.color
                ), shape = MaterialTheme.shapes.small
            )
            .clip(MaterialTheme.shapes.small)
    ) {
        TextWithDivider(
            text = "Correct Answer",
            result = "Hello World!",
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        TextWithDivider(
            text = "Your Answer",
            result = "Hello World!",
            backgroundColor = Color(0x1600FF00),
            disableDivider = true,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
fun MatchTheFollowingReview(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .border(
                border = BorderStroke(
                    width = DividerDefaults.Thickness,
                    color = DividerDefaults.color
                ), shape = MaterialTheme.shapes.small
            )
            .clip(MaterialTheme.shapes.small)
    ) {
        repeat(3) {
            Column(Modifier.weight(1f)) {
                repeat(4) { inner ->
                    val backgroundColor = when {
                        it != 0 && inner != 0 -> Color(0x1600FF00)
                        else -> Color.Unspecified
                    }
                    Box(
                        Modifier
                            .background(backgroundColor)
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "Column ${it + inner}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    if (inner != 3) HorizontalDivider()
                }
            }
            if (it != 2) VerticalDivider()
        }
    }
}

private fun customFormatNumber(value: Double): String {
    return if (value % 1.0 == 0.0) {
        value.toInt().toString()
    } else {
        value.toString()
    }
}

//@Preview
//@Composable
//fun ReviewViewPreview() {
//    SiddharoopaTheme(true) {
//        Surface {
//            ReviewView()
//        }
//    }
//}