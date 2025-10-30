package com.nxzef.sabdaroopa.ui.screen.quiz

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nxzef.sabdaroopa.R

@Composable
fun QuizInstructionScreen(
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier
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

            SectionView(stringResource(R.string.instruction_marking_system_title)) {
                BulletList(
                    listOf(
                        stringResource(R.string.instruction_marking_mcq),
                        stringResource(R.string.instruction_marking_mtf),
                        stringResource(R.string.instruction_marking_no_negative),
                        stringResource(R.string.instruction_marking_skip)
                    )
                )
            }

            SectionView(stringResource(R.string.instruction_categories_title)) {
                BulletList(
                    listOf(
                        stringResource(R.string.instruction_categories_general),
                        stringResource(R.string.instruction_categories_specific),
                        stringResource(R.string.instruction_categories_all)
                    )
                )
            }

            SectionView(stringResource(R.string.instruction_modes_title)) {
                BulletList(
                    listOf(
                        stringResource(R.string.instruction_modes_mcq),
                        stringResource(R.string.instruction_modes_mtf),
                        stringResource(R.string.instruction_modes_all)
                    )
                )
            }

            SectionView(stringResource(R.string.instruction_range_title)) {
                Text(
                    text = stringResource(R.string.instruction_range_description),
                    style = MaterialTheme.typography.bodyMedium
                )
                BulletList(
                    listOf(
                        stringResource(R.string.instruction_range_options)
                    )
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.instruction_range_example_label),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(R.string.instruction_range_example_text),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            SectionView(stringResource(R.string.instruction_mtf_notes_title)) {
                BulletList(
                    listOf(
                        stringResource(R.string.instruction_mtf_notes_trap),
                        stringResource(R.string.instruction_mtf_notes_extra),
                        stringResource(R.string.instruction_mtf_notes_marks),
                        stringResource(R.string.instruction_mtf_notes_partial)
                    )
                )
            }

            SectionView(stringResource(R.string.instruction_time_limit_title)) {
                BulletList(
                    listOf(
                        stringResource(R.string.instruction_time_no_limit),
                        stringResource(R.string.instruction_time_take_time)
                    )
                )
            }

            SectionView(stringResource(R.string.instruction_tips_title)) {
                BulletList(
                    listOf(
                        stringResource(R.string.instruction_tips_skip),
                        stringResource(R.string.instruction_tips_submit),
                        stringResource(R.string.instruction_tips_review)
                    )
                )
            }

            SectionView(stringResource(R.string.instruction_scoring_title)) {
                Text(
                    stringResource(R.string.instruction_scoring_example_label),
                    style = MaterialTheme.typography.bodyMedium
                )
                BulletList(
                    listOf(
                        stringResource(R.string.instruction_scoring_mcq),
                        stringResource(R.string.instruction_scoring_mtf),
                        stringResource(R.string.instruction_scoring_total)
                    )
                )
            }

            Button(
                onClick = onBackPress,
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .fillMaxWidth()
            ) {
                Text(stringResource(R.string.instruction_button_start))
            }
        }
    }
}

@Composable
fun BulletList(
    list: List<String>,
    modifier: Modifier = Modifier
) {
    val bullet = "\u2022"
    val textModifier = Modifier.alpha(0.9f)

    list.forEach { item ->
        Row(
            modifier = modifier.padding(vertical = 4.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = bullet,
                style = MaterialTheme.typography.bodyMedium,
                modifier = textModifier
            )
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
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable (ColumnScope.() -> Unit)
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