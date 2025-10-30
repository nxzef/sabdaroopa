package com.nxzef.sabdaroopa.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepSlider(
    defaultRange: Int,
    onValueChangeFinished: (Int) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Question Range",
    valueRange: ClosedFloatingPointRange<Float> = 5f..20f,
    steps: Int = 2,
    enabled: Boolean = true
) {
    val sliderState = remember(steps, valueRange) {
        SliderState(
            value = defaultRange.toFloat(),
            steps = steps,
            valueRange = valueRange
        )
    }

    // Track the last committed value
    var lastCommittedValue by remember { mutableIntStateOf(defaultRange) }

    // Sync with external changes
    LaunchedEffect(defaultRange) {
        if (sliderState.value.roundToInt() != defaultRange) {
            sliderState.value = defaultRange.toFloat()
            lastCommittedValue = defaultRange
        }
    }

    // Fire callback when value changes and user is not dragging
    LaunchedEffect(sliderState, onValueChangeFinished) {
        snapshotFlow { sliderState.value.roundToInt() to sliderState.isDragging }
            .distinctUntilChanged()
            .drop(1)  // Skip initial state
            .collect { (value, isDragging) ->
                if (!isDragging && value != lastCommittedValue) {
                    lastCommittedValue = value
                    onValueChangeFinished(value)
                }
            }
    }

    Column(
        modifier = modifier.padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(
                alpha = if (enabled) 1f else 0.38f
            )
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Slider(
                state = sliderState,
                enabled = enabled,
                thumb = { SliderThumb() },
                modifier = Modifier.weight(1f)
            )
            ValueBadge(
                value = sliderState.value.roundToInt(),
                enabled = enabled
            )
        }
    }
}

@Composable
private fun SliderThumb() {
    Box(
        Modifier
            .size(28.dp)
            .background(MaterialTheme.colorScheme.primary, CircleShape)
    )
}

@Composable
private fun ValueBadge(
    value: Int,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(
                    alpha = if (enabled) 1f else 0.38f
                ),
                shape = MaterialTheme.shapes.small
            )
            .padding(8.dp)
            .widthIn(min = 32.dp)
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                alpha = if (enabled) 1f else 0.38f
            )
        )
    }
}
