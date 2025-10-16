package com.nascriptone.siddharoopa.ui.screen.quiz

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nascriptone.siddharoopa.domain.DataSource
import com.nascriptone.siddharoopa.domain.SourceType
import com.nascriptone.siddharoopa.ui.component.CustomDialogDescription
import com.nascriptone.siddharoopa.ui.component.CustomDialogHead
import com.nascriptone.siddharoopa.ui.component.DialogLayout
import com.nascriptone.siddharoopa.ui.component.FilterBottomSheet
import com.nascriptone.siddharoopa.ui.screen.Navigation
import com.nascriptone.siddharoopa.ui.screen.Routes
import com.nascriptone.siddharoopa.ui.state.Filter
import kotlin.math.roundToInt

@Composable
fun QuizHomeScreen(
    quizViewModel: QuizViewModel,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    val uiState by quizViewModel.uiState.collectAsStateWithLifecycle()
    var sheetVisible by rememberSaveable { mutableStateOf(false) }
    var showProgress by rememberSaveable { mutableStateOf(false) }
    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    val currentFilter = when (val dataSource = uiState.dataSource) {
        is DataSource.Table -> dataSource.filter
        else -> Filter()
    }

    BackHandler(onBack = quizViewModel::onQuizHomeBack)

    LaunchedEffect(Unit) {
        quizViewModel.uiEvents.collect { hasData ->
            if (hasData) showExitDialog = true
            else navHostController.navigateUp()
        }
    }

    Surface {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            QuizChooseOptionView(
                title = "Question Source"
            ) {
                SourceType.entries.forEach { type ->
                    QuizChooseOption(
                        name = stringResource(type.labelResId),
                        selected = type == uiState.dataSource.type,
                        onClick = { quizViewModel.switchSource(sourceType = type) }
                    ) { startSpace ->
                        DataView(
                            startSpace = startSpace,
                            dataSource = uiState.dataSource,
                            onClick = {
                                when (type) {
                                    SourceType.TABLE -> sheetVisible = !sheetVisible
                                    SourceType.FAVORITES -> {
                                        navHostController.navigate(Navigation.Favorites.name)
                                    }

                                    SourceType.CUSTOM_LIST -> {
                                        navHostController.navigate(Navigation.Home.name)
                                    }
                                }
                            })
                    }
                    if (type.ordinal < SourceType.entries.lastIndex) HorizontalDivider()
                }
            }
            Spacer(Modifier.height(12.dp))
            QuizChooseOptionView(
                title = "Choose Mode"
            ) {
                Mode.entries.forEach { mode ->
                    QuizChooseOption(
                        name = stringResource(mode.uiName),
                        selected = mode == uiState.mode,
                        onClick = { quizViewModel.updateMode(mode) })
                    if (mode.ordinal < Mode.entries.lastIndex) HorizontalDivider()
                }
            }
            StepSlider(
                defaultRange = uiState.range, onValueChangeFinished = quizViewModel::updateRange
            )
            Spacer(Modifier.height(28.dp))
            Button(
                onClick = {
                    showProgress = true
                    quizViewModel.createQuizQuestions()
                }, modifier = Modifier.fillMaxWidth()
            ) { Text("Begin Quiz") }
            Spacer(Modifier.height(TopAppBarDefaults.TopAppBarExpandedHeight))
        }
        FilterBottomSheet(
            visible = sheetVisible,
            currentFilter = currentFilter,
            onDismissRequest = { sheetVisible = false },
            onApplyFilter = quizViewModel::onApplyFilter
        )
        CreationProgress(
            visible = showProgress,
            creationState = uiState.creationState,
            onDismissRequest = { showProgress = false },
            onRetryClick = quizViewModel::createQuizQuestions,
            onSuccess = {
                showProgress = false
                navHostController.navigate(Routes.QuizQuestion.withRoot)
            })
        HomeExitDialog(
            visible = showExitDialog,
            onExit = {
                showExitDialog = false
                navHostController.navigateUp()
            },
            onDismissRequest = { showExitDialog = false })
    }
}

@Composable
fun DataView(
    startSpace: Dp,
    dataSource: DataSource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                enabled = dataSource.type != SourceType.TABLE,
                onClick = onClick
            )
            .then(
                if (dataSource.type != SourceType.TABLE) Modifier.padding(
                    start = startSpace, end = 16.dp
                )
                else Modifier.padding(
                    start = startSpace, end = 16.dp, bottom = 16.dp
                )
            )
    ) {
        when (dataSource) {
            is DataSource.Table -> {
                val filter = dataSource.filter
                val chips = remember(filter) {
                    listOfNotNull(
                        filter.category?.skt, filter.sound?.skt, filter.gender?.skt
                    )
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    itemVerticalAlignment = Alignment.CenterVertically
                ) {
                    if (chips.isEmpty()) AppliedChip(
                        label = "Default", onClick = onClick, default = true
                    )
                    else chips.forEach { resId ->
                        AppliedChip(
                            label = stringResource(resId), onClick = onClick
                        )
                    }
                    IconButton(onClick = onClick) {
                        Icon(Icons.Rounded.FilterList, null)
                    }
                }
            }

            else -> {
                val data = dataSource.ids
                if (data.isEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 24.dp)
                    ) {
                        Icon(Icons.Rounded.Add, null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Add Items", style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    Column(modifier = Modifier.padding(vertical = 12.dp)) {
                        Text(
                            text = "${data.size} ${if (data.size == 1) "Item" else "Items"} Added",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = dataSource.display,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(8.dp))
                            Icon(Icons.Rounded.ChevronRight, null)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppliedChip(
    label: String, onClick: () -> Unit, modifier: Modifier = Modifier, default: Boolean = false
) {
    val color: Color = if (default) MaterialTheme.colorScheme.onTertiaryContainer
    else MaterialTheme.colorScheme.onSecondaryContainer
    val backgroundColor: Color = if (default) MaterialTheme.colorScheme.tertiaryContainer
    else MaterialTheme.colorScheme.secondaryContainer

    Text(
        text = label,
        color = color,
        style = MaterialTheme.typography.labelMedium,
        modifier = modifier
            .background(
                color = backgroundColor, shape = CircleShape
            )
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    )
}

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
    var currentValue by rememberSaveable { mutableIntStateOf(defaultRange) }

    val sliderState = rememberSliderState(
        value = currentValue.toFloat(), steps = steps, onValueChangeFinished = {
            onValueChangeFinished(currentValue)
        }, valueRange = valueRange
    )

    LaunchedEffect(sliderState.value) {
        currentValue = sliderState.value.roundToInt()
    }

    Column(
        modifier = modifier.padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = if (enabled) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            }
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Slider(
                state = sliderState, enabled = enabled, thumb = {
                    Box(
                        Modifier
                            .size(28.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    )
                }, modifier = Modifier.weight(1f)
            )

            ValueBadge(
                value = currentValue, enabled = enabled
            )
        }
    }
}

@Composable
private fun ValueBadge(
    value: Int, enabled: Boolean, modifier: Modifier = Modifier
) {
    val backgroundColor: Color = if (enabled) MaterialTheme.colorScheme.surfaceVariant
    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f)
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(color = backgroundColor, shape = MaterialTheme.shapes.small)
            .padding(8.dp)
            .widthIn(min = 32.dp)
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = if (enabled) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
            }
        )
    }
}

@Composable
fun QuizChooseOptionView(
    title: String, modifier: Modifier = Modifier, content: @Composable (ColumnScope.() -> Unit)
) {
    Column(
        modifier = modifier.padding(vertical = 20.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = MaterialTheme.shapes.large
                )
                .clip(MaterialTheme.shapes.large)
        ) { content() }
    }
}

@Composable
fun QuizChooseOption(
    name: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    dataView: @Composable ((startSpace: Dp) -> Unit)? = null,
) {

    val density = LocalDensity.current
    var radioButtonWidthPx by remember { mutableIntStateOf(0) }
    val startSpace = with(density) {
        radioButtonWidthPx.toDp() + 12.dp
    }

    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .animateContentSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick,
                modifier = Modifier.onSizeChanged { radioButtonWidthPx = it.width })
            Spacer(Modifier.width(8.dp))
            Text(
                text = name,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        if (selected && dataView != null) dataView.invoke(startSpace)
    }
}

@Composable
fun HomeExitDialog(
    visible: Boolean,
    onExit: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!visible) return
    Dialog(onDismissRequest = onDismissRequest) {
        DialogLayout {
            Column(modifier = modifier.padding(20.dp)) {
                CustomDialogHead("Exit Quiz?")
                Spacer(Modifier.height(4.dp))
                CustomDialogDescription(
                    text = "If you exit now, all changes will be lost.\nDo you still want to exit?"
                )
                Spacer(Modifier.height(24.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("CANCEL")
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = onExit) {
                        Text("EXIT")
                    }
                }
            }
        }
    }
}