package com.nascriptone.siddharoopa.ui.component

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.ui.theme.SabdaroopaTheme

@Composable
fun CurrentState(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable (BoxScope.() -> Unit)
) {
    Surface {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = contentAlignment,
            content = content
        )
    }
}

@Preview
@Composable
fun CurrentStatePreview() {
    SabdaroopaTheme {
        CurrentState {
            Text("Hello World!")
        }
    }
}


@Composable
fun getSupportingText(sabda: Sabda): String {
    val sbd = stringResource(R.string.sabda)
    val gdr = stringResource(sabda.gender.skt)
    return "${sabda.anta} $gdr \"${sabda.word}\" $sbd"
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun CustomDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    showDefaultAction: Boolean = false,
    onConfirm: () -> Unit = {},
    onCancel: () -> Unit = {},
    head: @Composable (() -> Unit)? = null,
    description: @Composable (() -> Unit)? = null,
    action: @Composable (() -> Unit)? = null,
) {
    if (!visible) return
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val dialogWidth = when (configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> screenWidth * 0.86f
        else -> minOf(screenWidth * 0.6f, 600.dp)
    }
    Dialog(onDismissRequest) {
        Column(
            modifier = modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = MaterialTheme.shapes.large
                )
                .width(dialogWidth)
                .padding(20.dp)
        ) {
            head?.invoke()
            Spacer(Modifier.height(8.dp))
            description?.invoke()
            Spacer(Modifier.height(32.dp))
            if (showDefaultAction) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier.align(Alignment.End)
                ) {
                    TextButton(onClick = onCancel) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = onConfirm) {
                        Text("OK")
                    }
                }
            }
            action?.invoke()
        }
    }
}

@Composable
fun CustomDialogHead(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(text, style = MaterialTheme.typography.titleMedium, modifier = modifier)
}

@Composable
fun CustomDialogDescription(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier.alpha(0.8f)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomToolTip(
    text: String,
    modifier: Modifier = Modifier,
    content: @Composable (() -> Unit)
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Below),
        tooltip = {
            PlainTooltip { Text(text) }
        },
        state = rememberTooltipState(),
        modifier = modifier,
        content = content
    )
}