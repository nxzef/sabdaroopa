package com.nascriptone.siddharoopa.ui.screen.quiz

import android.util.Log
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.nascriptone.siddharoopa.ui.theme.SiddharoopaTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
    modifier: Modifier = Modifier,
    activeColor: Color = Color.Blue,
    idleColor: Color = Color.DarkGray,
    animationDuration: Int = 200
) {
    val xOffset = remember { Animatable(0f) }
    val yOffset = remember { Animatable(0f) }

    var isDragging by rememberSaveable { mutableStateOf(false) }
    var zIndex by rememberSaveable { mutableFloatStateOf(0f) }
    var boxSize by remember { mutableStateOf(IntSize.Zero) }
    var parentBoxSize by remember { mutableStateOf<IntSize?>(IntSize.Zero) }
    var boxOffsetInParentY by rememberSaveable { mutableStateOf<Float?>(null) }

    val boxWidth = boxSize.width.toFloat()
    val boxHeight = boxSize.height.toFloat()
    val xExtra = boxWidth / 4
    val yExtra = boxHeight / 3

    val parentBoxHeight = parentBoxSize?.height?.toFloat()!!
    val maxOffset = parentBoxHeight - boxHeight

    val backgroundColor by animateColorAsState(
        targetValue = if (isDragging) activeColor else idleColor,
        animationSpec = tween(durationMillis = 200),
        label = "boxBackgroundColor"
    )

    Log.d("yOffset", "$parentBoxHeight")

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .onGloballyPositioned {
                boxSize = it.size
                parentBoxSize = it.parentLayoutCoordinates?.size
                if (boxOffsetInParentY == null) boxOffsetInParentY = it.positionInParent().y
            }
            .offset {
                IntOffset(
                    xOffset.value.roundToInt(),
                    yOffset.value.roundToInt()
                )
            }
            .fillMaxWidth()
            .height(64.dp)
            .background(backgroundColor)
            .zIndex(zIndex)
            .pointerInput(Unit) {
                coroutineScope {
                    detectDragGestures(
                        onDragStart = {
                            isDragging = true
                            zIndex = 1f
                        },
                        onDragEnd = {
                            launch {
                                listOf(
                                    async { xOffset.animateTo(0F, tween(animationDuration)) },
                                    async {

                                        // Work in Progress
                                        val targetOffset = 0F
                                        // Work in Progress


                                        yOffset.animateTo(targetOffset, tween(animationDuration))
                                    }
                                ).awaitAll()

                                isDragging = false
                                zIndex = 0f
                            }
                        },
                        onDrag = { change, dragAmount ->
                            launch {
                                change.consume()

                                val newX = (xOffset.value + dragAmount.x)
                                    .coerceIn(-xExtra, xExtra)
                                val newY = (yOffset.value + dragAmount.y)
                                    .coerceIn(
                                        -(boxOffsetInParentY!! + yExtra),
                                        (maxOffset - boxOffsetInParentY!!) + yExtra
                                    )

                                xOffset.snapTo(newX)
                                yOffset.snapTo(newY)
                            }
                        }
                    )
                }
            }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


@Preview
@Composable
fun QuizInstructScreenPreview() {
    SiddharoopaTheme {
        QuizInstructionScreen(Modifier.fillMaxSize())
    }
}
