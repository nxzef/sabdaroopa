package com.nascriptone.siddharoopa.ui.screen.quiz

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.nascriptone.siddharoopa.ui.theme.SiddharoopaTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
fun QuizInstructionScreen(
    modifier: Modifier = Modifier
) {

    val list = List(3) { "Box ${it + 1}" }

    Surface(modifier) {
        Column {
            Text("Hello World this is instruction screen!")
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    Modifier
                        .width(140.dp)
                        .border(
                            width = 0.5.dp,
                            color = Color.LightGray
                        )
                        .zIndex(0F)
                ) {
                    list.forEachIndexed { index, item ->
                        DraggableBox(text = item)
                    }
                }

            }
        }
    }
}

@Composable
fun DraggableBox(
    text: String,
    modifier: Modifier = Modifier
) {

    val xOffset = remember { Animatable(0F) }
//    val yOffset = remember { Animatable(0F) }
    var isDragging by rememberSaveable { mutableStateOf(false) }
    var zIndex by rememberSaveable { mutableFloatStateOf(0F) }


    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .offset {
                IntOffset(
                    xOffset.value.roundToInt(),
                    0
                )
            }
            .fillMaxWidth()
            .height(64.dp)
            .background(if (isDragging) Color.Blue else Color.DarkGray)
            .zIndex(zIndex)
            .then(modifier)
            .pointerInput(Unit) {
                coroutineScope {
                    detectDragGestures(
                        onDragStart = {
                            isDragging = true
                            zIndex = 1F
                        },
                        onDragEnd = {
                            isDragging = false
                            zIndex = 0F
                            launch {
                                xOffset.animateTo(
                                    0F,
                                    tween(200)
                                )
                            }
//                            launch {
//                                yOffset.animateTo(
//                                    0F,
//                                    tween(300)
//                                )
//                            }

                        }
                    ) { change, dragAmount ->
                        launch {
                            change.consume()

                            val xAmount = xOffset.value + dragAmount.x
                            val xFinalValue = xAmount - (xAmount / 100F)
                            xOffset.snapTo(xFinalValue)
//                            yOffset.snapTo(yOffset.value + dragAmount.y)
                        }
                    }
                }
            }
    ) {
        Text(text, color = Color.White)
    }
}


@Preview
@Composable
fun QuizInstructScreenPreview() {
    SiddharoopaTheme {
        QuizInstructionScreen(Modifier.fillMaxSize())
    }
}
