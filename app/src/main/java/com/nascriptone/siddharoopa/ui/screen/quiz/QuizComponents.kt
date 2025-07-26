package com.nascriptone.siddharoopa.ui.screen.quiz

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nascriptone.siddharoopa.data.model.uiobj.CaseName
import com.nascriptone.siddharoopa.data.model.uiobj.FormName


// Regex Text
@Composable
fun RegexText(
    @StringRes template: Int, key: Map<String, String>, modifier: Modifier = Modifier
) {
    val mutableKey = key.toMutableMap()
    val stringRes = stringResource(template)
    val vibaktiKey = "vibhakti"
    val vachanaKey = "vachana"
    if (mutableKey.containsKey(vibaktiKey) || mutableKey.containsKey(vachanaKey)) {
        val vibaktiValue = key.getValue(vibaktiKey)
        val vachanaValue = key.getValue(vachanaKey)
        val vibaktiEnum = CaseName.valueOf(vibaktiValue)
        val vachanaEnum = FormName.valueOf(vachanaValue)
        val vibaktiSktName = stringResource(vibaktiEnum.sktName)
        val vachanaSktName = stringResource(vachanaEnum.sktName)
        mutableKey[vibaktiKey] = vibaktiSktName
        mutableKey[vachanaKey] = vachanaSktName
    }
    val finalKey = mutableKey.toMap()
    val text = replacePlaceholders(stringRes, finalKey)
    Text(text, modifier = modifier, style = MaterialTheme.typography.titleLarge)
}

// Common View Layout

@Composable
fun ModeView(
    title: String,
    modifier: Modifier = Modifier,
    disableBackgroundColor: Boolean = false,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Column(modifier = modifier.padding(vertical = 12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = if (!disableBackgroundColor) MaterialTheme.colorScheme.surfaceContainer
                    else Color.Unspecified,
                    shape = MaterialTheme.shapes.large
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) { content() }
    }
}

// Text with a divider under the text

@Composable
fun TextWithDivider(
    text: String,
    result: String? = "12",
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Unspecified,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    disableDivider: Boolean = false
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .then(modifier)
    ) {
        Text(
            text = "$text:",
            style = style,
        )
        result?.let {
            Text(
                text = it,
                style = style
            )
        }
    }
    if (!disableDivider) HorizontalDivider()
}

@Composable
fun QuestionWithNumber(
    questionNumber: Int,
    modifier: Modifier = Modifier,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Spacer(Modifier.height(20.dp))
    Row(modifier) {
        Box(
            modifier = Modifier
                .width(32.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Text("$questionNumber.", fontWeight = FontWeight.Medium)
        }
        Column(
            Modifier
                .padding(start = 12.dp)
//                .clip(MaterialTheme.shapes.small)

        ) {
            Text(
                "What is the dual nominative form of some kind of sabda?",
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
    Spacer(Modifier.height(20.dp))
    HorizontalDivider()
}

private fun replacePlaceholders(template: String, values: Map<String, String>): String {
    return template.replace(Regex("\\{(\\w+)\\}")) { match ->
        values[match.groupValues[1]] ?: match.value
    }
}