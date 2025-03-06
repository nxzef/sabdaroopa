package com.nascriptone.siddharoopa.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nascriptone.siddharoopa.ui.theme.SiddharoopaTheme

@Composable
fun CurrentState(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
    ) {
        Box(
            contentAlignment = contentAlignment
        ) {
            content()
        }
    }
}

@Preview
@Composable
fun CurrentStatePreview() {
    SiddharoopaTheme {
        CurrentState {
            Text("Hello World!")
        }
    }
}