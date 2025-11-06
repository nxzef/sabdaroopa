package com.nxzef.sabdaroopa.ui.screen.quiz

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
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nxzef.sabdaroopa.ui.component.CustomDialogDescription
import com.nxzef.sabdaroopa.ui.component.CustomDialogHead
import com.nxzef.sabdaroopa.ui.component.DialogLayout
import com.nxzef.sabdaroopa.ui.theme.ExtendedMaterialTheme


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
private fun MultipleChoiceReview(
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
private fun MultipleChoiceReviewBox(
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
        status -> ExtendedMaterialTheme.success.colorContainer
        else -> ExtendedMaterialTheme.wrong.colorContainer
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
    modifier: Modifier = Modifier
) {

    val options = mtfGeneratedData.options
    val trueOption = mtfGeneratedData.trueOption
    val answer = mtfGeneratedData.answer
    val columns = mergeCollectionsAsColumns(options, trueOption, answer)

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
        columns.forEachIndexed { outerIndex, column ->
            Column(Modifier.weight(1f)) {
                column.forEachIndexed { innerIndex, cell ->

                    val isHeader = innerIndex == 0 && cell == null

                    val idleColor: Color =
                        if (outerIndex != 0 && answer != null && innerIndex < 4) {
                            val afterIndex = (innerIndex - 1).coerceAtLeast(0)
                            if (trueOption[afterIndex] == answer[afterIndex]) ExtendedMaterialTheme.success.colorContainer
                            else ExtendedMaterialTheme.wrong.colorContainer
                        } else MaterialTheme.colorScheme.surfaceContainer

                    val backgroundColor: Color =
                        if (isHeader) MaterialTheme.colorScheme.surfaceContainerHighest
                        else idleColor


                    val fillNull: String = if (isHeader && outerIndex == 0) "Keys"
                    else if (isHeader && outerIndex == 1) "Correct"
                    else if (isHeader && outerIndex == 2) "Yours"
                    else if (!isHeader && outerIndex == 2 && cell == null) "Skipped"
                    else ""
                    val text: String = cell ?: fillNull

                    Box(
                        Modifier
                            .background(backgroundColor)
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                            .padding(8.dp)
                    ) {
                        Text(text, style = MaterialTheme.typography.bodyMedium)
                    }
                    if (innerIndex != column.size - 1) HorizontalDivider()
                }
            }
            if (outerIndex != columns.size - 1) VerticalDivider()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreationProgress(
    visible: Boolean,
    creationState: CreationState,
    onDismissRequest: () -> Unit,
    onRetryClick: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!visible) return
    if (creationState is CreationState.Success) LaunchedEffect(Unit) { onSuccess() }
    else BasicAlertDialog(
        onDismissRequest = onDismissRequest, modifier = modifier
    ) {
        DialogLayout {
            when (creationState) {
                is CreationState.Loading -> Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(20.dp)
                ) {
                    CircularProgressIndicator()
                    Spacer(Modifier.width(16.dp))
                    Text("Preparing...")
                }

                is CreationState.Error -> Column(modifier = Modifier.padding(16.dp)) {
                    CustomDialogHead("Error")
                    Spacer(Modifier.height(4.dp))
                    CustomDialogDescription(creationState.message)
                    Spacer(Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = onDismissRequest) {
                            Text("Cancel")
                        }
                        Spacer(Modifier.width(8.dp))
                        TextButton(onClick = onRetryClick) {
                            Text("Retry")
                        }
                    }
                }

                else -> Unit
            }
        }
    }
}


private fun mergeCollectionsAsColumns(
    options: Map<String?, String>,
    trueOption: List<String>,
    answer: List<String>?
): List<List<String?>> {
    val safeList1 = trueOption.take(3) + List(3 - trueOption.size.coerceAtMost(3)) { null }

    val actualList2 = answer ?: List(3) { null }
    val safeList2: List<String?> =
        actualList2.take(3) + List(3 - actualList2.size.coerceAtMost(3)) { null }

    val col1 = options.keys.toList().withSingleNullFirst()
    val col2 = safeList1.withSingleNullFirst()
    val col3 = safeList2.withSingleNullFirst()

    return listOf(col1, col2, col3)
}

private fun <T> List<T>.withSingleNullFirst(): List<T?> {
    return listOf<T?>(null) + if (this.all { it == null }) this else this.filterNotNull()
}


sealed interface Res {
    data class InInt(val int: Int) : Res
    data class InStr(val str: String) : Res
}