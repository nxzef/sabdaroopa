package com.nascriptone.siddharoopa.ui.screen.quiz

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.ui.component.CurrentState
import com.nascriptone.siddharoopa.ui.screen.SiddharoopaRoutes
import com.nascriptone.siddharoopa.viewmodel.SiddharoopaViewModel
import kotlin.math.roundToInt

@Composable
fun QuizResultScreen(
    viewModel: SiddharoopaViewModel,
    quizSectionState: QuizSectionState,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {

    var valuated by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!valuated) {
            viewModel.quizValuation()
            valuated = true
        }
    }
    when (val data = quizSectionState.result) {
        is ValuationState.Calculate -> CurrentState {
            CircularProgressIndicator()
        }

        is ValuationState.Error -> CurrentState {
            Text(data.message)
        }

        is ValuationState.Success -> ResultScreenMainContent(
            result = data.result,
            navHostController = navHostController,
            modifier = modifier
        )
    }
}

@Composable
fun ResultScreenMainContent(
    result: Result,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    val dashboard = result.dashboard
    val mcqStats = result.mcqStats
    val mtfStats = result.mtfStats
    val summary = result.summary

    val screenTitle = stringResource(R.string.quiz_result_title)

    Surface {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            Text(
                text = screenTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 20.dp)
            )
            MainDashboard(dashboard)
            Spacer(Modifier.height(12.dp))
            if (mcqStats != null) {
                MultipleChoiceQuestion(mcqStats)
            }
            if (mtfStats != null) {
                MatchTheFollowing(mtfStats)
            }
            if (summary != null) {
                FinalSummary(summary)
            }
            ReviewView(
                items = result.finalData,
                onClick = {
                    navHostController.navigate(SiddharoopaRoutes.QuizReview.name)
                }
            )
            Spacer(Modifier.height(40.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .fillMaxWidth()
            ) {
                OutlinedButton(onClick = {}, modifier = Modifier.weight(1f)) {
                    Text("QUIT")
                }
                Spacer(Modifier.width(12.dp))
                Button(onClick = {}, modifier = Modifier.weight(1f)) {
                    Text("RETRY")
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

    var accuracy by rememberSaveable { mutableFloatStateOf(0f) }

    val currentBarProgress by animateFloatAsState(
        targetValue = accuracy / 100f,
        animationSpec = tween(2000)
    )

    val currentProgress by animateIntAsState(
        targetValue = accuracy.roundToInt(),
        animationSpec = tween(2000)
    )


    LaunchedEffect(Unit) {
        accuracy = dashboard.accuracy
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { currentBarProgress },
                strokeWidth = 8.dp,
                modifier = Modifier.size(116.dp)
            )
            Text(
                text = "$currentProgress",
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
            message = stringResource(dashboard.message)
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
                text = "${dashboard.score} / ${dashboard.totalPossibleScore}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
fun FinalSummary(
    summary: Summary,
    modifier: Modifier = Modifier,
) {

    val style = MaterialTheme.typography.bodyMedium.copy(
        fontWeight = FontWeight.Medium
    )

    ModeView(
        title = "Final Summary",
        modifier = modifier
    ) {
        TextWithDivider("Total Questions", res = Res.InInt(summary.totalQuestions), style = style)
        TextWithDivider(
            "Total Possible Marks",
            res = Res.InInt(summary.totalPossibleScore),
            style = style
        )
        TextWithDivider("Total Score", res = Res.InInt(summary.score), style = style)
        TextWithDivider(
            "Accuracy",
            res = Res.InStr(summary.accuracy.toString()),
            style = style,
            disableDivider = true
        )
    }
}

@Composable
fun MultipleChoiceQuestion(
    mcqStats: McqStats,
    modifier: Modifier = Modifier
) {
    val (totalQuestions, attended, skipped, correct, wrong, score) = mcqStats
    ModeView(
        title = "Multiple Choice (MCQ)",
        modifier = modifier
    ) {
        TextWithDivider("Number of Questions", Res.InInt(totalQuestions))
        TextWithDivider("Attended", Res.InInt(attended))
        TextWithDivider("Skipped", Res.InInt(skipped))
        TextWithDivider("Correct Answers", Res.InInt(correct))
        TextWithDivider("Wrong Answers", Res.InInt(wrong))
        TextWithDivider("MCQ Score", Res.InInt(score), disableDivider = true)
    }
}

@Composable
fun MatchTheFollowing(
    mtfStats: MtfStats,
    modifier: Modifier = Modifier
) {
    val (totalSet, totalPairs, attended, skipped, correct, wrong, score) = mtfStats
    ModeView(
        title = "Match the Following",
        modifier = modifier
    ) {
        TextWithDivider("Number of Sets", Res.InInt(totalSet))
        TextWithDivider("Pairs to Match", Res.InInt(totalPairs))
        TextWithDivider("Attended Sets", Res.InInt(attended))
        TextWithDivider("Skipped Sets", Res.InInt(skipped))
        TextWithDivider("Correct Matches", Res.InInt(correct))
        TextWithDivider("Wrong Matches", Res.InInt(wrong))
        TextWithDivider("MTF Score", Res.InInt(score), disableDivider = true)
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

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun ReviewView(
    items: List<QuestionOption>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModeView(
        title = "Quiz Review",
        topLayer = {
            FadeEndView(onClick)
        },
        modifier = modifier
            .clickable(onClick = onClick)
    ) {
        items.forEach { item ->
            QuestionAnswerReview(item, items.lastIndex)
        }
    }
}

@Composable
fun ModeView(
    title: String,
    modifier: Modifier = Modifier,
    disableBackgroundColor: Boolean = false,
    topLayer: @Composable (() -> Unit)? = null,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.height(8.dp))
        Box(
            propagateMinConstraints = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = if (!disableBackgroundColor) MaterialTheme.colorScheme.surfaceContainer
                        else Color.Unspecified,
                        shape = MaterialTheme.shapes.large
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .then(modifier)
            ) { content() }
            topLayer?.invoke()
        }
    }
}

@Composable
fun FadeEndView(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color.Unspecified,
                        MaterialTheme.colorScheme.surfaceDim
                    ),
                ),
                shape = MaterialTheme.shapes.large.copy(
                    topStart = CornerSize(0),
                    topEnd = CornerSize(0)
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Button(onClick) {
            Text("View All")
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                modifier = Modifier.size(20.dp),
                contentDescription = null
            )
        }
    }
}