package com.nascriptone.siddharoopa.ui.screen.quiz

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nascriptone.siddharoopa.ui.theme.SiddharoopaTheme


@Composable
fun QuizInstructionScreen(
    modifier: Modifier = Modifier
) {

    val list = MutableList(3) { "Box $it" }

    Surface(modifier) {
        Column {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                DraggableBoxColumn(list)
            }

        }
    }
}

@Composable
fun DraggableBoxColumn(
    list: List<String>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .width(140.dp)

    ) {
        list.forEachIndexed { index, text ->
            Text(text)
//            DraggableBox(text, index)
        }
    }
}

//@Composable
//fun DraggableBox(
//    text: String,
//    index: Int,
//    modifier: Modifier = Modifier,
//    activeColor: Color = Color.Magenta,
//    idleColor: Color = Color.Gray,
//    animationDuration: Int = 200
//) {
//    val xOffset = remember { Animatable(0f) }
//    val yOffset = remember { Animatable(0f) }
//
//    var isDragging by rememberSaveable { mutableStateOf(false) }
//    var zIndex by rememberSaveable { mutableFloatStateOf(0f) }
//    var boxSize by remember { mutableStateOf(IntSize.Zero) }
//    var parentBoxSize by remember { mutableStateOf(IntSize.Zero) }
//
//    val boxWidth = boxSize.width.toFloat()
//    val boxHeight = boxSize.height.toFloat()
//    val xExtra = boxWidth / 8
//    val yExtra = boxHeight / 3
//
//    val parentBoxHeight = parentBoxSize.height.toFloat()
//    val componentOffset = boxHeight * index
//    val maxOffset = parentBoxHeight - boxHeight
//
//    val backgroundColor by animateColorAsState(
//        targetValue = if (isDragging) activeColor else idleColor,
//        animationSpec = tween(durationMillis = 200),
//        label = "boxBackgroundColor"
//    )
//
//    Log.d(
//        "triggerTest",
//        "::::::::::: >> $text Triggered!!! Offset: $componentOffset, Index: $index"
//    )
//
//    LaunchedEffect(Unit) {
////        yOffset.animateTo(0F, tween(animationDuration))
//    }
//
//    Box(
//        contentAlignment = Alignment.Center,
//        modifier = modifier
//            .fillMaxWidth()
//            .height(64.dp)
//            .offset {
//                IntOffset(
//                    xOffset.value.roundToInt(),
//                    yOffset.value.roundToInt()
//                )
//            }
//            .onGloballyPositioned {
//                boxSize = it.size
//                it.parentLayoutCoordinates?.let { c -> parentBoxSize = c.size }
//            }
//            .background(backgroundColor)
//            .zIndex(zIndex)
//            .pointerInput(Unit) {
//                coroutineScope {
//                    detectDragGestures(
//                        onDragStart = {
//                            isDragging = true
//                            zIndex = 1f
//                        },
//                        onDragEnd = {
//                            launch {
//                                listOf(
//                                    async { xOffset.animateTo(0F, tween(animationDuration)) },
//                                    async {
//
//                                        // Work in Progress
//
//
//                                        val currentOffset = yOffset.value + componentOffset
//                                        val actualOffset = currentOffset + (boxHeight / 2)
//
////                                        val targetOffset =
////                                            if (actualOffset < boxHeight) 0F - boxOffsetInParentY
////                                            else if (actualOffset < maxOffset) boxHeight - boxOffsetInParentY
////                                            else maxOffset - boxOffsetInParentY
//
//
//                                        // Work in Progress
//
//                                        Log.d("swapFunction", "Swap I:$index, Swap J: ${actualOffset.roundToInt() / boxHeight.roundToInt()}")
//
//
//                                        yOffset.animateTo(0F, tween(animationDuration))
//                                    }
//                                ).awaitAll()
//
//                                isDragging = false
//                                zIndex = 0f
//                            }
//                        },
//                        onDrag = { change, dragAmount ->
//                            launch {
//                                change.consume()
//
//                                // X Axis Logic
//                                val currentX = xOffset.value
//                                val xNormalized = (currentX / xExtra).coerceIn(-1F, 1F)
//                                val xResistance = 1F - xNormalized.absoluteValue
//                                val xDrag = currentX + dragAmount.x * xResistance
//
//
//                                // Y Axis Logic
//                                val currentY = yOffset.value
//                                val yFrom = 0F - componentOffset
//                                val yTo = maxOffset - componentOffset
//
//                                val belowMin = (yFrom - currentY).coerceAtLeast(0f)
//                                val aboveMax = (currentY - yTo).coerceAtLeast(0f)
//                                val overshoot = belowMin + aboveMax
//
//                                val yNormalized = (overshoot / yExtra).coerceIn(0f, 1f)
//                                val yResistance = 1F - yNormalized.absoluteValue
//                                val yDrag = currentY + dragAmount.y * yResistance
//
//                                val newX = xDrag
//                                    .coerceIn(-xExtra, xExtra)
//                                val newY = yDrag
//                                    .coerceIn(
//                                        -(yExtra + componentOffset),
//                                        (maxOffset - componentOffset) + yExtra
//                                    )
//
//
//                                xOffset.snapTo(newX)
//                                yOffset.snapTo(newY)
//                            }
//                        }
//                    )
//                }
//            }
//    ) {
//        Text(
//            text = text,
//            style = MaterialTheme.typography.bodyMedium
//        )
//    }
//}

@Composable
fun CustomRow(modifier: Modifier = Modifier) {
    Column {
        Row(
            modifier = modifier
                .padding(8.dp)
                .height(100.dp)
                .border(
                    border = _root_ide_package_.androidx.compose.foundation.BorderStroke(
                        width = DividerDefaults.Thickness,
                        color = DividerDefaults.color
                    ), shape = CardDefaults.outlinedShape
                )
        ) {
            Text("Hello World!")
            VerticalDivider()
            Text("Hellllllo")
        }
        OutlinedCard {
            Text("Hello World")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomRowPreview() {
    SiddharoopaTheme {
        CustomRow()
    }
}
