package com.nascriptone.siddharoopa.ui.screen.quiz

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.data.model.Category
import com.nascriptone.siddharoopa.data.model.Gender
import com.nascriptone.siddharoopa.data.model.Sound
import com.nascriptone.siddharoopa.domain.Source
import com.nascriptone.siddharoopa.domain.SourceWithData
import com.nascriptone.siddharoopa.ui.component.CustomDialogDescription
import com.nascriptone.siddharoopa.ui.component.CustomDialogHead
import com.nascriptone.siddharoopa.ui.component.CustomToolTip
import com.nascriptone.siddharoopa.ui.component.DialogLayout
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
    val filter = when (val sourceWithData = uiState.sourceWithData) {
        is SourceWithData.FromTable -> sourceWithData.filter
        else -> Filter()
    }

    LaunchedEffect(Unit) { }

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
                Source.entries.forEach { source ->
                    QuizChooseOption(
                        name = stringResource(source.uiName),
                        selected = source == uiState.sourceWithData.source,
                        onClick = {
                            quizViewModel.switchSource(
                                sourceWithData = source.createSourceData()
                            )
                        }) { startSpace ->
                        DataView(
                            startSpace = startSpace,
                            sourceWithData = uiState.sourceWithData,
                            onClick = {
                                when (source) {
                                    Source.FROM_TABLE -> sheetVisible = !sheetVisible
                                    Source.FROM_FAVORITES -> {
                                        navHostController.navigate(Navigation.Favorites.name)
                                    }
                                    Source.FROM_LIST -> {}
                                }
                            })
                    }
                    if (source.ordinal < Source.entries.lastIndex) HorizontalDivider()
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
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Begin Quiz") }
            Spacer(Modifier.height(TopAppBarDefaults.TopAppBarExpandedHeight))
        }
        TableModalBottomSheet(
            visible = sheetVisible,
            filter = filter,
            onFilterChange = quizViewModel::updateFilter,
            onDismissRequest = { sheetVisible = !sheetVisible })
        if (showProgress) CreationProgress(
            creationState = uiState.creationState,
            onDismissRequest = { showProgress = false },
            onRetryClick = quizViewModel::createQuizQuestions,
            onSuccess = {
                navHostController.navigate(Routes.QuizQuestion.withRoot)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreationProgress(
    creationState: CreationState,
    onDismissRequest: () -> Unit,
    onRetryClick: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (creationState is CreationState.Success) LaunchedEffect(Unit) { onSuccess() }
    else BasicAlertDialog(
        onDismissRequest = onDismissRequest, modifier = modifier
    ) {
        DialogLayout {
            when (creationState) {
                is CreationState.Loading -> Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(20.dp)
                ) {
                    CircularProgressIndicator()
                    Spacer(Modifier.width(16.dp))
                    Text("Preparing...")
                }

                is CreationState.Error -> Column(modifier = Modifier.padding(16.dp)) {
                    CustomDialogHead("Error")
                    Spacer(Modifier.height(4.dp))
                    CustomDialogDescription(creationState.message)
                    Spacer(Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = onDismissRequest) {
                            Text("Cancel")
                        }
                        Spacer(Modifier.width(8.dp))
                        TextButton(onClick = onRetryClick) {
                            Text("Retry")
                        }
                    }
                }

                else -> Unit
            }
        }
    }
}

@Composable
fun DataView(
    startSpace: Dp,
    sourceWithData: SourceWithData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                enabled = sourceWithData.source != Source.FROM_TABLE, onClick = onClick
            )
            .then(
                if (sourceWithData.source != Source.FROM_TABLE) Modifier.padding(
                    start = startSpace, end = 16.dp
                )
                else Modifier.padding(
                    start = startSpace, end = 16.dp, bottom = 16.dp
                )
            )
    ) {
        when (sourceWithData) {
            is SourceWithData.FromTable -> {
                val filter = sourceWithData.filter
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
                val data = sourceWithData.data
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
                                text = sourceWithData.display,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableModalBottomSheet(
    visible: Boolean,
    filter: Filter,
    onFilterChange: (Filter) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!visible) return
    var category by remember { mutableStateOf(filter.category) }
    var sound by remember { mutableStateOf(filter.sound) }
    var gender by remember { mutableStateOf(filter.gender) }
    val bottomSheetState = rememberModalBottomSheetState(true)

    val newFilter by remember(
        category, sound, gender
    ) {
        derivedStateOf {
            Filter(
                category = category, sound = sound, gender = gender
            )
        }
    }
    LaunchedEffect(newFilter) { onFilterChange(newFilter) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest, sheetState = bottomSheetState
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            IconButton(
                onClick = {
                    category = null
                    sound = null
                    gender = null
                }, modifier = Modifier.align(Alignment.End)
            ) {
                CustomToolTip("Reset") { Icon(Icons.Rounded.Refresh, null) }
            }
            FilterChipRow(
                label = "Categories",
                filters = categories,
                selected = category,
                onSelectedChanged = { category = it }) { filter ->
                stringResource(filter?.skt ?: R.string.all_skt)
            }
            FilterChipRow(
                label = "Sounds",
                filters = sounds,
                selected = sound,
                onSelectedChanged = { sound = it }) { filter ->
                stringResource(filter?.skt ?: R.string.all_skt)
            }
            FilterChipRow(
                label = "Genders",
                filters = genders,
                selected = gender,
                onSelectedChanged = { gender = it }) { filter ->
                stringResource(filter?.skt ?: R.string.all_skt)
            }
            Spacer(Modifier.height(56.dp))
        }
    }
}

@Composable
fun <T> FilterChipRow(
    label: String,
    filters: Set<T?>,
    selected: T?,
    onSelectedChanged: (T?) -> Unit,
    modifier: Modifier = Modifier,
    labelMapper: @Composable (T?) -> String
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            filters.forEach { filter ->
                val selected = selected == filter
                FilterChip(
                    selected = selected,
                    onClick = { onSelectedChanged(filter) },
                    label = { Text(labelMapper(filter)) },
                    leadingIcon = {
                        AnimatedVisibility(selected) {
                            Icon(Icons.Rounded.Check, null)
                        }
                    },
                )
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

val categories = setOf(null) + Category.entries
val sounds = setOf(null) + Sound.entries
val genders = setOf(null) + Gender.entries