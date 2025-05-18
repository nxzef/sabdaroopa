package com.nascriptone.siddharoopa.ui.screen.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt


@Composable
fun QuizInstructionScreen(
    modifier: Modifier = Modifier
) {
    Surface(modifier) {
        Column {
            Text("Hello World this is instruction screen!")
            DraggableComposable()
            DragAndDropExample()
        }
    }
}

@Composable
fun DraggableComposable() {
    // State to track the offset of the composable
    val offsetX = remember { mutableFloatStateOf(0f) }
    val offsetY = remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.floatValue.roundToInt(), offsetY.floatValue.roundToInt()) }
            .size(100.dp)
            .background(Color.Blue)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume() // Consume the gesture event
                    offsetX.floatValue += dragAmount.x
                    offsetY.floatValue += dragAmount.y
                }
            }
    )
}

@Composable
fun DragAndDropExample() {
    val offsetX = remember { mutableFloatStateOf(0f) }
    val offsetY = remember { mutableFloatStateOf(0f) }
    val isDragging = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart
    ) {
        // Drop Target
        Box(
            modifier = Modifier
                .offset(x = 200.dp, y = 400.dp)
                .size(150.dp)
                .background(Color.Green)
        )

        // Draggable Composable
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        offsetX.floatValue.roundToInt(),
                        offsetY.floatValue.roundToInt()
                    )
                }
                .size(100.dp)
                .background(if (isDragging.value) Color.Red else Color.Blue)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { isDragging.value = true },
                        onDragEnd = {
                            isDragging.value = false
                            // Check if dropped in target (simplified)
                            if (offsetX.floatValue > 200 && offsetX.floatValue < 350 &&
                                offsetY.floatValue > 400 && offsetY.floatValue < 550
                            ) {
                                offsetX.floatValue = 225f // Snap to target center
                                offsetY.floatValue = 425f
                            }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            offsetX.floatValue += dragAmount.x
                            offsetY.floatValue += dragAmount.y
                        }
                    )
                }
        )
    }
}