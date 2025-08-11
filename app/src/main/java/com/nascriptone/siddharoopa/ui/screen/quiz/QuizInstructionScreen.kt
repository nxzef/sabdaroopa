package com.nascriptone.siddharoopa.ui.screen.quiz

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.ui.theme.SabdaroopaTheme

@Composable
fun QuizInstructionScreen(
    onBackPress: () -> Unit, modifier: Modifier = Modifier
) {
    Surface {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.instruction_intro_title),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = stringResource(R.string.instruction_intro),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .alpha(0.8f)
            )
            Spacer(Modifier.height(16.dp))
            SectionView("Marking System") {
                BulletList(
                    listOf(
                        "MCQ: 2 marks for each correct answer",
                        "MTF: 1 mark per correct match (up to 3 marks/set)",
                        "No negative marking",
                        "Skipped questions don’t affect your score"
                    )
                )
            }
            SectionView("Quiz Categories") {
                BulletList(
                    listOf(
                        "General Sabda Table — Standard words across all types",
                        "Specific Sabda Table — Topic-focused words (e.g., special categories)",
                        "You can also choose \"All\" to mix questions from both tables"
                    )
                )
            }
            SectionView("Quiz Modes") {
                BulletList(
                    listOf(
                        "MCQ Only — Multiple Choice Questions only",
                        "MTF Only — Match the Following only",
                        "All Categories — Mixes both types:\n" + "→ 70% MCQ & 30% MTF"
                    )
                )
            }
            SectionView("Question Range Selection") {
                Text(
                    text = "Choose how many questions you want:",
                    style = MaterialTheme.typography.bodyMedium
                )
                BulletList(
                    listOf(
                        "Options: 5, 10, 15, 20 (Default: 10)"
                    )
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Example:", style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "If you choose “All Categories” with 10 questions:\n" + "→ You’ll get 7 MCQs + 3 MTF sets",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            SectionView("Special Notes (MTF)") {
                BulletList(
                    listOf(
                        "Some MTF sets have 4 options but only 3 correct matches",
                        "The extra option is a “trap” and not part of scoring",
                        "Each correct match = 1 mark",
                        "Partial marks are possible (e.g., 2 correct = 2 marks)"
                    )
                )
            }
            SectionView("Time Limit") {
                BulletList(
                    listOf(
                        "No time restrictions",
                        "Take your time to understand and answer each question"
                    )
                )
            }
            SectionView("Answering Tips") {
                BulletList(
                    listOf(
                        "You can skip any question if you're unsure",
                        "Once you submit, you’ll see your score + detailed stats",
                        "Review your weak areas to improve faster"
                    )
                )
            }
            SectionView("Scoring Example") {
                Text(
                    "Example Quiz: 10 Questions (All Mode)",
                    style = MaterialTheme.typography.bodyMedium
                )
                BulletList(
                    listOf(
                        "7 MCQs → 5 correct → 10 marks",
                        "3 MTF sets → 7 correct matches → 7 marks",
                        "Total Score = 17 / 35"
                    )
                )
            }
            Button(
                onClick = onBackPress, modifier = Modifier
                    .padding(vertical = 20.dp)
                    .fillMaxWidth()
            ) {
                Text("Got It! Let’s Start Quiz")
            }
        }
    }
}

@Composable
fun BulletList(
    list: List<String>, modifier: Modifier = Modifier
) {
    val bullet = "\u2022"
    val textModifier = Modifier.alpha(0.9f)
    list.forEach { item ->
        Row(modifier.padding(vertical = 4.dp)) {
            Box {
                Text(
                    text = bullet,// "•"
                    style = MaterialTheme.typography.titleMedium,
                    modifier = textModifier
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = item,
                style = MaterialTheme.typography.bodyMedium,
                modifier = textModifier
            )
        }
    }
}

@Composable
fun SectionView(
    title: String, modifier: Modifier = Modifier, content: @Composable (ColumnScope.() -> Unit)
) {
    Column(
        modifier = modifier.padding(vertical = 16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.height(8.dp))
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun QuizInstructionScreenPreview() {
    SabdaroopaTheme(true) {
        QuizInstructionScreen(onBackPress = {})
    }
}
