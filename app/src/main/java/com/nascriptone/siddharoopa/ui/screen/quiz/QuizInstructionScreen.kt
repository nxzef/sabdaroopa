package com.nascriptone.siddharoopa.ui.screen.quiz

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.nascriptone.siddharoopa.ui.theme.SiddharoopaTheme


@Composable
fun QuizInstructionScreen(
    modifier: Modifier = Modifier
) {
    Surface(modifier) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .width(IntrinsicSize.Min)
                    .height(IntrinsicSize.Min),
                propagateMinConstraints = true
            ) {
                Box(
                    Modifier
//                        .fillMaxSize()
//                        .background(Color.Blue)
                        .border(
                            border = BorderStroke(
                                width = DividerDefaults.Thickness,
                                color = Color.Red
                            ),
                            shape = MaterialTheme.shapes.extraLarge
                        ),
                )
                Row(
                    Modifier
//                        .background(Color.Red)
                ) {
                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    x = 0,
                                    y = 0
                                )
                            }
//                            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                            .size(140.dp)
                            .zIndex(0F)
                    ) {
                        Text("Hello")
                    }
                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    x = 0,
                                    y = 0
                                )
                            }
//                            .background(MaterialTheme.colorScheme.error)
                            .size(140.dp)
                            .zIndex(0F)
                    ) {
                        Text("Hello")
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CustomRowPreview() {
    SiddharoopaTheme {
        QuizInstructionScreen()
    }
}
