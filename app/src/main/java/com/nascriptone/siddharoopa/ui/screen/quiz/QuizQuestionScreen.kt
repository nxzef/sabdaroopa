package com.nascriptone.siddharoopa.ui.screen.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.nascriptone.siddharoopa.data.model.uiobj.CaseName
import com.nascriptone.siddharoopa.data.model.uiobj.FormName
import com.nascriptone.siddharoopa.data.model.uiobj.Gender
import com.nascriptone.siddharoopa.ui.component.CurrentState
import com.nascriptone.siddharoopa.ui.screen.SiddharoopaRoutes
import com.nascriptone.siddharoopa.viewmodel.SiddharoopaViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Collections
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun QuizQuestionScreen(
    quizSectionState: QuizSectionState,
    viewModel: SiddharoopaViewModel,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {

    var isFetched by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!isFetched) {
            viewModel.createQuizQuestions()
            isFetched = true
        }
    }

    when (val result = quizSectionState.questionList) {
        is CreationState.Loading -> CurrentState {
            CircularProgressIndicator()
        }

        is CreationState.Error -> CurrentState {
            Text(text = result.message)
        }

        is CreationState.Success -> QuizQuestionScreenContent(
            viewModel = viewModel,
            data = result.data,
            quizSectionState = quizSectionState,
            navHostController = navHostController,
            modifier = modifier
        )
    }
}

@Composable
fun QuizQuestionScreenContent(
    viewModel: SiddharoopaViewModel,
    data: List<QuestionOption>,
    quizSectionState: QuizSectionState,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {

    var questionIndex by rememberSaveable { mutableIntStateOf(0) }
    var enabled by rememberSaveable { mutableStateOf(true) }
    val questionCount = quizSectionState.questionRange
    val lastQuestionIndex = questionCount - 1
    val scope = rememberCoroutineScope()


    fun onClick(id: Int, action: Action) {
        scope.launch {
            enabled = false
            viewModel.updateAnswer(id, action)
            delay(1000)
            if (questionIndex < lastQuestionIndex) {
                questionIndex++
            } else {
                navHostController.navigate(SiddharoopaRoutes.QuizResult.name)
            }
        }
    }

    Surface {
        Column(
            modifier =
                modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Question ${questionIndex + 1} of $questionCount",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 20.dp)
            )
            Box(
                modifier = Modifier.weight(1f),
                propagateMinConstraints = true
            ) {
                data.forEachIndexed { index, questionOption ->
                    QuestionOption(
                        isVisible = index == questionIndex,
                        enabled = enabled,
                        questionOption = questionOption,
                        onValueChange = { answer ->
                            viewModel.updateCurrentAnswer(answer)
                        },
                        onLaunch = { enabled = it },
//                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.padding(vertical = 20.dp)
            ) {
                OutlinedButton(
                    enabled = enabled,
                    onClick = { onClick(questionIndex, Action.SKIP) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("SKIP")
                }
                Spacer(Modifier.width(12.dp))
                Button(
                    enabled = quizSectionState.currentAnswer !is Answer.Unspecified,
                    onClick = { onClick(questionIndex, Action.SUBMIT) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        if (questionIndex == lastQuestionIndex) "SUBMIT"
                        else "NEXT"
                    )
                }
            }
        }
    }
}

@Composable
fun QuestionOption(
    isVisible: Boolean,
    enabled: Boolean,
    questionOption: QuestionOption,
    onLaunch: (Boolean) -> Unit,
    onValueChange: (Answer) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        isVisible,
        enter = slideInHorizontally(
            initialOffsetX = { (it * 70) / 100 }) + fadeIn(),
        exit = slideOutHorizontally(
            targetOffsetX = { -it / 4 }) + fadeOut(),
    ) {

        LaunchedEffect(Unit) { onLaunch(true) }

        Column(
            modifier = Modifier
                .then(modifier)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(70.dp))
            when (val state = questionOption.state) {
                is State.McqState -> StateMcq(
                    state = state,
                    enabled = enabled,
                    onValueChange = { onValueChange(it) }
                )

                is State.MtfState -> StateMtf(
                    state = state,
                    enabled = enabled,
                    onValueChange = { onValueChange(it) }
                )
            }
        }
    }
}

@Composable
fun StateMcq(
    state: State.McqState,
    enabled: Boolean,
    onValueChange: (Answer) -> Unit,
    modifier: Modifier = Modifier
) {
    val template = state.data.template
    val templateKey = state.data.templateKey
    val options = state.data.options
    val trueOption = state.data.trueOption
    val answer = state.data.answer

    var currentAnswer by rememberSaveable { mutableStateOf(answer) }

    RegexText(template, templateKey)
    Spacer(Modifier.height(40.dp))
    options.forEachIndexed { i, option ->
        val isCorrectOption = option == trueOption && answer != null
        val isWrongSelectedOption = answer != null && answer == option && !isCorrectOption
        val backgroundColor = when {
            isCorrectOption -> Color(0x1600FF00)
            isWrongSelectedOption -> Color(0x16FF0000)
            else -> Color.Unspecified
        }
        val highlight = isCorrectOption || isWrongSelectedOption != enabled
        val selected = remember(enabled, currentAnswer) {
            currentAnswer = if (enabled) currentAnswer else null
            currentAnswer == option
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(backgroundColor)
                .selectable(
                    enabled = highlight,
                    selected = selected,
                    onClick = {
                        currentAnswer = option
                        onValueChange(
                            Answer.Mcq(option)
                        )
                    })
                .padding(16.dp)
        ) {
            OptionIcon(
                correct = isCorrectOption,
                wrong = isWrongSelectedOption,
                modifier = Modifier.padding(0.dp)
            ) {
                RadioButton(
                    enabled = enabled,
                    selected = selected,
                    onClick = null,
                )
            }
            Spacer(Modifier.width(16.dp))
            OptionText(
                text = option,
                enabled = highlight
            )
        }
    }
}

@Composable
fun StateMtf(
    state: State.MtfState,
    enabled: Boolean,
    onValueChange: (Answer) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val template = state.data.template
    val templateKey = state.data.templateKey
    val keys = remember(state) { state.data.options.keys.toList() }
    val values = remember(state) { state.data.options.values.toList() }
    val trueOption = remember(state) { state.data.trueOption }
    val answer = state.data.answer

    val thickness = 4.dp
    val animationDuration = 120
    val shape = CardDefaults.outlinedShape as RoundedCornerShape
    val cornerSize = shape.topStart
    val dividerThickness = remember(thickness, density) {
        with(density) { thickness.toPx() }
    }

    val sharedParams = ColumnParams(
        answer = answer,
        trueOption = trueOption,
        cornerSize = cornerSize,
        dividerThickness = dividerThickness,
        animationDuration = animationDuration
    )

    RegexText(template, templateKey)
    Spacer(Modifier.height(40.dp))
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min), propagateMinConstraints = true
    ) {
        Box(
            Modifier
                .background(
                    MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = CardDefaults.outlinedShape
                )
                .border(
                    border = BorderStroke(
                        width = thickness, color = DividerDefaults.color
                    ), shape = CardDefaults.outlinedShape
                )
        )
        Row(Modifier.padding(thickness)) {
            KeyColumn(
                keys = keys,
                enabled = enabled,
                params = sharedParams,
                thickness = thickness,
                modifier = Modifier.weight(1f)
            )
            VerticalDivider(thickness = thickness)
            ValueColumn(
                values = values,
                enabled = enabled,
                params = sharedParams,
                thickness = thickness,
                onValueChange = { onValueChange(it) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun KeyColumn(
    keys: List<String?>,
    enabled: Boolean,
    params: ColumnParams,
    thickness: Dp,
    modifier: Modifier = Modifier
) {

    val density = LocalDensity.current
    val (answer, trueOption, cornerSize, dividerThickness, animationDuration) = params

    Column(modifier = modifier) {

        keys.forEachIndexed { index, key ->

            val boxSize = remember { mutableStateOf(Size.Zero) }
            val cornerSizePx = remember(boxSize.value) {
                cornerSize.toPx(boxSize.value, density)
            }
            val singleCornerSize = remember(cornerSizePx) {
                cornerSizePx - dividerThickness
            }

            val color = if (index < 3 && answer != null) {
                val match = trueOption[index] == answer[index]
                if (match) Color(0x1600FF00) else Color(0x16FF0000)
            } else MaterialTheme.colorScheme.surfaceContainerLow
            val backgroundColor by animateColorAsState(
                targetValue = color,
                animationSpec = tween(animationDuration),
                label = "keyBoxBackgroundColor"
            )
            val topStart = remember { if (index == 0) singleCornerSize else 0f }
            val bottomStart = remember {
                if (index == keys.lastIndex) singleCornerSize else 0f
            }

            Box(
                modifier = Modifier
                    .background(
                        color = backgroundColor, shape = RoundedCornerShape(
                            topStart = topStart, bottomStart = bottomStart
                        )
                    )
                    .fillMaxWidth()
                    .height(64.dp)
                    .onSizeChanged { boxSize.value = it.toSize() },
                contentAlignment = Alignment.CenterStart
            ) {
                if (key != null) {
                    OptionText(
                        key,
                        enabled = enabled,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
            if (index != keys.lastIndex) HorizontalDivider(thickness = thickness)
        }
    }
}

@Composable
fun ValueColumn(
    values: List<String>,
    enabled: Boolean,
    params: ColumnParams,
    thickness: Dp,
    onValueChange: (Answer) -> Unit,
    modifier: Modifier = Modifier
) {

    val list = rememberSaveable(
        saver = listSaver(save = { it.toList() }, restore = { it.toMutableStateList() })
    ) { values.toMutableStateList() }
    val visualOrder by remember(list, values) {
        derivedStateOf { list.toFastIndexOfList(values) }
    }

    val containerSize = remember { mutableStateOf(Size.Zero) }

    Column(
        modifier = modifier.onSizeChanged {
            containerSize.value = it.toSize()
        }) {
        values.forEachIndexed { index, value ->
            val logicalPosition = visualOrder.getOrElse(index) { return@forEachIndexed }

            DraggableBox(
                index = index,
                logicalPosition = logicalPosition,
                value = value,
                enabled = enabled,
                params = params,
                containerSize = containerSize.value,
                lastIndex = values.lastIndex,
                onChange = { i, j ->
                    if (i != j) {
                        Collections.swap(list, i, j)
                        onValueChange(Answer.Mtf(list))
                    }
                })

            if (index != values.lastIndex) HorizontalDivider(thickness = thickness)
        }
    }
}


@Composable
fun DraggableBox(
    index: Int,
    logicalPosition: Int,
    value: String,
    enabled: Boolean,
    params: ColumnParams,
    containerSize: Size,
    lastIndex: Int,
    onChange: (lgp: Int, drop: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    val xOffset = remember { Animatable(0f) }
    val yOffset = remember { Animatable(0f) }

    val currentLogicalPosition by rememberUpdatedState(logicalPosition)
    val (answer, trueOption, cornerSize, dividerThickness, animationDuration) = params
    var isDragging by rememberSaveable { mutableStateOf(false) }
    var zIndex by rememberSaveable { mutableFloatStateOf(0f) }
    var count by rememberSaveable { mutableIntStateOf(0) }

    val boxSize = remember { mutableStateOf(Size.Zero) }
    val cornerSizePx = remember(boxSize.value) {
        cornerSize.toPx(boxSize.value, density)
    }
    val singleCornerSize = remember(cornerSizePx) {
        cornerSizePx - dividerThickness
    }
    val halfDivider = remember(dividerThickness) { dividerThickness / 2 }
    val boxWidth = remember(boxSize.value) { boxSize.value.width }
    val boxHeight = remember(boxSize.value) { boxSize.value.height }
    val extraX = remember(boxWidth) { boxWidth / 8 }
    val extraY = remember(boxHeight) { boxHeight / 3 }

    val parentBoxHeight = remember(containerSize) { containerSize.height }
    val dividerGap = remember(
        index, dividerThickness
    ) { dividerThickness * index }
    val componentOffset = remember(boxHeight, index, dividerGap) {
        boxHeight * index + dividerGap
    }
    val maxOffset = remember(parentBoxHeight, boxHeight) {
        parentBoxHeight - boxHeight
    }
    val singleSpace = remember(boxHeight, dividerThickness) {
        boxHeight + dividerThickness
    }

    val activeColor = MaterialTheme.colorScheme.surfaceContainerHighest
    val idleColor: Color = if (currentLogicalPosition < 3 && answer != null) {
        val exactAnswer = answer[currentLogicalPosition]
        val trueAnswer = trueOption[currentLogicalPosition]
        if (exactAnswer == trueAnswer) Color(0x1600FF00)
        else Color(0x16FF0000)
    } else MaterialTheme.colorScheme.surfaceContainerHigh
    val backgroundColor by animateColorAsState(
        targetValue = if (isDragging) activeColor else idleColor,
        animationSpec = tween(animationDuration),
        label = "boxBackgroundColor"
    )
    val topEnd by animateFloatAsState(
        targetValue = if (currentLogicalPosition == 0) singleCornerSize else 0f,
        animationSpec = tween(animationDuration),
        label = "boxTopEndShape"
    )
    val bottomEnd by animateFloatAsState(
        targetValue = if (currentLogicalPosition == lastIndex) singleCornerSize else 0f,
        animationSpec = tween(animationDuration),
        label = "boxBottomEndShape"
    )

    suspend fun translate(
        offset: Animatable<Float, AnimationVector1D>,
        delta: Float,
    ) {
        if (!isDragging) zIndex = 0.5f
        offset.animateTo(
            delta, tween(animationDuration)
        )
        isDragging = false
        zIndex = 0f
    }

    fun handleDrag(change: PointerInputChange, dragAmount: Offset) {
        scope.launch {
            change.consume()

            // X Axis Logic
            val currentX = xOffset.value
            val xNormalized = (currentX / extraX).coerceIn(-1f, 1f)
            val xResistance = 1f - xNormalized.absoluteValue
            val xDrag = currentX + dragAmount.x * xResistance


            // Y Axis Logic
            val currentY = yOffset.value
            val yFrom = 0f - componentOffset
            val yTo = maxOffset - componentOffset

            val belowMin = (yFrom - currentY).coerceAtLeast(0f)
            val aboveMax = (currentY - yTo).coerceAtLeast(0f)
            val overshoot = belowMin + aboveMax

            val yNormalized = (overshoot / extraY).coerceIn(0f, 1f)
            val yResistance = 1f - yNormalized
            val yDrag = currentY + dragAmount.y * yResistance

            val newX = xDrag.coerceIn(-extraX, extraX)
            val newY = yDrag.coerceIn(
                -(extraY + componentOffset), (maxOffset - componentOffset) + extraY
            )


            xOffset.snapTo(newX)
            yOffset.snapTo(newY)
        }
    }

    fun handleDragEnd() {
        scope.launch {
            async {
                translate(xOffset, 0f)
            }
            async {
                val currentOffset = yOffset.value + componentOffset + halfDivider
                val middlePointer = currentOffset + (singleSpace / 2)
                val drop = middlePointer.roundToInt() / singleSpace.roundToInt()
                onChange(currentLogicalPosition, drop)
                count++
            }
        }
    }

    val target = computeAnimateTo(index, currentLogicalPosition, boxHeight, dividerThickness)

    LaunchedEffect(count, target) {
        runCatching {
            translate(yOffset, target)
        }.onFailure { error -> error.printStackTrace() }
    }

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .offset {
                IntOffset(
                    xOffset.value.roundToInt(), yOffset.value.roundToInt()
                )
            }
            .onSizeChanged { boxSize.value = it.toSize() }
            .background(
                color = backgroundColor, shape = RoundedCornerShape(
                    topEnd = topEnd, bottomEnd = bottomEnd
                )
            )
            .zIndex(zIndex)
            .then(
                if (enabled) Modifier
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                isDragging = true
                                zIndex = 1f
                            }, onDrag = ::handleDrag, onDragEnd = ::handleDragEnd
                        )
                    }
                else Modifier
            )
    ) {
        OptionText(
            text = value,
            enabled = enabled,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun OptionIcon(
    correct: Boolean,
    wrong: Boolean,
    modifier: Modifier = Modifier,
    default: @Composable () -> Unit
) {
    val enterTransition: EnterTransition = fadeIn() + scaleIn()
    val exitTransition: ExitTransition = fadeOut() + scaleOut()
    Box(modifier) {
        AnimatedVisibility(
            correct, enter = enterTransition, exit = exitTransition
        ) {
            Icon(
                Icons.Filled.CheckCircle, null, tint = Color.Green
            )
        }
        AnimatedVisibility(
            wrong, enter = enterTransition, exit = exitTransition
        ) {
            Icon(
                Icons.Filled.Cancel, null, tint = Color.Red
            )
        }
        AnimatedVisibility(
            !correct && !wrong, enter = enterTransition, exit = exitTransition
        ) { default() }
    }
}


@Composable
fun OptionText(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: TextStyle = MaterialTheme.typography.titleLarge
) {
    val keyText = text.uppercase()
    val adjustedText = when (keyText) {
        in caseNameStrings -> {
            runCatching {
                stringResource(enumValueOf<CaseName>(keyText).sktName)
            }.getOrDefault(
                keyText
            )
        }

        in genderNameStrings -> {
            runCatching {
                stringResource(enumValueOf<Gender>(keyText).skt)
            }.getOrDefault(
                keyText
            )
        }

        in formNameStrings -> {
            runCatching {
                stringResource(enumValueOf<FormName>(keyText).sktName)
            }.getOrDefault(
                keyText
            )
        }

        else -> keyText
    }
    Text(
        adjustedText,
        style = style,
        modifier = Modifier
            .alpha(if (enabled) 1f else 0.4f)
            .then(modifier)
    )
}

data class ColumnParams(
    val answer: List<String>?,
    val trueOption: List<String>,
    val cornerSize: CornerSize,
    val dividerThickness: Float,
    val animationDuration: Int
)


private val caseNameStrings = enumValues<CaseName>().map { it.name }.toSet()
private val genderNameStrings = enumValues<Gender>().map { it.name }.toSet()
private val formNameStrings = enumValues<FormName>().map { it.name }.toSet()


private fun computeAnimateTo(
    index: Int,
    logicalPosition: Int,
    boxHeight: Float,
    dividerThickness: Float
): Float {
    val diff = logicalPosition - index
    val cdg = dividerThickness * diff
    return boxHeight * diff + cdg
}

private fun <T> List<T>.toFastIndexOfList(original: List<T>): List<Int> {
    val org = original.withIndex().associate { it.value to it.index }
    return this.mapIndexed { i, v -> v to i }.sortedBy { org[it.first] }.map { it.second }
}