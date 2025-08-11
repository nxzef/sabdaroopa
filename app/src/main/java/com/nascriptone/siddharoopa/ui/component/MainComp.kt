package com.nascriptone.siddharoopa.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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