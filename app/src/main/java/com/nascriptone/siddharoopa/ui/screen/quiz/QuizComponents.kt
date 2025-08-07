package com.nascriptone.siddharoopa.ui.screen.quiz

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


// Regex Text
@Composable
fun QuestionText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleLarge
) {
    Text(
        text = text,
        style = style,
        fontWeight = FontWeight.Medium,
        modifier = modifier
    )
}


// Text with a divider under the text

@Composable
fun TextWithDivider(
    text: String,
    res: Res,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Unspecified,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    disableDivider: Boolean = false,
) {

    val result: String = when (res) {
        is Res.InInt -> "${res.int}"
        is Res.InStr -> res.str
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(vertical = 4.dp)
            .then(modifier)
    ) {
        Text(
            text = "$text:",
            style = style,
        )
        Text(
            text = result,
            style = style
        )
    }
    if (!disableDivider) HorizontalDivider()
}


@Composable
fun QuestionWithNumber(
    questionWithNumber: QuestionWithNumber,
    lastIndex: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Column(modifier = modifier) {
        Row {
            Box(
                modifier = Modifier
                    .width(28.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Text(
                    text = "${questionWithNumber.number + 1}.",
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text(questionWithNumber.question, style = style)
                Spacer(Modifier.height(8.dp))
                content()
            }
        }
        if (questionWithNumber.number != lastIndex) HorizontalDivider(Modifier.padding(vertical = 20.dp))
    }
}

@Composable
fun QuestionAnswerReview(
    item: QuestionOption,
    lastIndex: Int,
    modifier: Modifier = Modifier
) {
    QuestionWithNumber(
        questionWithNumber = item.questionWithNumber,
        lastIndex = lastIndex,
        modifier = modifier
    ) {
        when (val state = item.option) {
            is Option.McqOption -> MultipleChoiceReview(state.mcqGeneratedData)
            is Option.MtfOption -> MatchTheFollowingReview(state.mtfGeneratedData)
        }
    }
}

@Composable
fun MultipleChoiceReview(
    mcqGeneratedData: McqGeneratedData,
    modifier: Modifier = Modifier,
) {

    val correct = mcqGeneratedData.trueOption
    val answer = mcqGeneratedData.answer

    val status = answer?.let { it == correct }

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
        MultipleChoiceReviewBox(
            label = "Correct Answer",
            value = correct,
            isHead = true
        )
        HorizontalDivider()
        MultipleChoiceReviewBox(
            label = "Your Answer",
            value = answer ?: "Skipped",
            status = status
        )
    }
}

@Composable
fun MultipleChoiceReviewBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isHead: Boolean = false,
    status: Boolean? = null,
    style: TextStyle = MaterialTheme.typography.bodyMedium
) {

    val backgroundColor = when {
        isHead -> Color.Unspecified
        status == null -> MaterialTheme.colorScheme.surfaceContainerHighest
        status -> Color(0x1600FF00)
        else -> Color(0x16FF0000)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(backgroundColor)
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) { Text(label, style = style) }
        VerticalDivider()
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) { Text(value, style = style) }
    }
}

@Composable
fun MatchTheFollowingReview(
    mtfGeneratedData: MtfGeneratedData,
    modifier: Modifier = Modifier,
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

sealed interface Res {
    data class InInt(val int: Int) : Res
    data class InStr(val str: String) : Res
}