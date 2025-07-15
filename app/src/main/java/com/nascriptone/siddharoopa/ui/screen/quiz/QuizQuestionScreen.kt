package com.nascriptone.siddharoopa.ui.screen.quiz

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
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
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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

    LaunchedEffect(Unit) {
        viewModel.createQuizQuestions()
    }

    when (val result = quizSectionState.result) {
        is CreationState.Loading -> CurrentState {
            CircularProgressIndicator()
        }

        is CreationState.Error -> CurrentState {
            Text(result.msg)
        }

        is CreationState.Success -> QuizQuestionScreenContent(
            viewModel, result.data, quizSectionState, navHostController, modifier
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
    val questionCount = quizSectionState.questionRange.toInt()
    val lastQuestionIndex = questionCount - 1
    val scope = rememberCoroutineScope()
    var enabled by rememberSaveable { mutableStateOf(false) }

    Surface {
        Column(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Spacer(Modifier.height(24.dp))
                Text(
                    "Question ${questionIndex + 1} of $questionCount",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Box(
                contentAlignment = Alignment.Center
            ) {
                data.forEachIndexed { index, each ->
                    QuestionOption(
                        isVisible = index == questionIndex, each = each, onValueChange = { answer ->
                            viewModel.updateCurrentAnswer(answer)
                            enabled = true
                        }, currentAnswer = quizSectionState.currentAnswer, enabled = enabled
                    )
                }

            }
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                if (questionIndex < lastQuestionIndex) {
                                    questionIndex++
                                } else {
                                    navHostController.navigate(SiddharoopaRoutes.QuizResult.name)
                                }
                            }
                        }, modifier = Modifier.weight(1F)
                    ) {
                        Text("SKIP")
                    }
                    Spacer(Modifier.width(12.dp))
                    Button(
                        enabled = quizSectionState.currentAnswer != Answer.Unspecified && enabled,
                        onClick = {
                            scope.launch {
                                enabled = false
                                viewModel.submitAnswer(questionIndex)
                                delay(1000)
                                if (questionIndex == lastQuestionIndex) {
                                    navHostController.navigate(SiddharoopaRoutes.QuizResult.name)
                                } else {
                                    questionIndex++
                                }
                            }
                        },
                        modifier = Modifier.weight(1F)
                    ) {
                        Text(
                            if (questionIndex == lastQuestionIndex) "SUBMIT"
                            else "NEXT"
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}


@Composable
fun QuestionOption(
    isVisible: Boolean,
    each: QuestionOption,
    currentAnswer: Answer,
    enabled: Boolean,
    onValueChange: (Answer) -> Unit,
    modifier: Modifier = Modifier
) {

    AnimatedVisibility(
        isVisible, enter = slideInHorizontally(
            initialOffsetX = { (it * 70) / 100 }) + fadeIn(), exit = slideOutHorizontally(
            targetOffsetX = { -it / 4 }) + fadeOut(), modifier = modifier
    ) {

        val exactAnswer = each.answer
        LaunchedEffect(Unit) {
            onValueChange(exactAnswer)
        }

        Column(
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            when (val state = each.option) {
                is Option.McqOption -> {
                    val options = state.data.options
                    RegexText(each.question, state.data.questionKey)
                    Spacer(Modifier.height(40.dp))
                    options.forEachIndexed { i, option ->
                        val isCorrectOption =
                            option == state.data.trueOption && exactAnswer is Answer.Mcq
                        val isWrongSelectedOption =
                            exactAnswer is Answer.Mcq && exactAnswer.ans == option && !isCorrectOption
                        val backgroundColor = when {
                            isCorrectOption -> Color(0x1600FF00)
                            isWrongSelectedOption -> Color(0x16FF0000)
                            else -> Color.Transparent
                        }
                        val selected =
                            if (currentAnswer is Answer.Mcq) option == currentAnswer.ans else false
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .background(backgroundColor)
                                .selectable(
                                    enabled = enabled, selected = selected, onClick = {
                                        if (enabled) onValueChange(
                                            Answer.Mcq(option)
                                        )
                                    })
                                .padding(8.dp)
                        ) {
                            OptionIcon(
                                isCorrectOption,
                                isWrongSelectedOption,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                RadioButton(
                                    enabled = enabled,
                                    selected = selected,
                                    onClick = null,
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            OptionText(text = option)
                        }
                    }
                }

                is Option.MtfOption -> {

                    val density = LocalDensity.current

                    val options = state.data.options
                    val trueOptions = state.data.trueOption
                    val keys = options.map { it.key }
                    val values = options.map { it.value }
                    val trueValues = trueOptions.map { it.value }

                    val thickness = 4.dp
                    val shape = CardDefaults.outlinedShape
                    val cornerSize = (shape as RoundedCornerShape).topStart
                    val dividerThickness = with(density) { thickness.toPx() }
                    val animationDuration = 120

                    RegexText(each.question, state.data.questionKey)
                    Spacer(Modifier.height(40.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min),
                        propagateMinConstraints = true
                    ) {
                        Box(
                            Modifier
                                .background(
                                    MaterialTheme.colorScheme.surfaceContainerLow,
                                    shape = CardDefaults.outlinedShape
                                )
                                .border(
                                    border = BorderStroke(
                                        width = thickness, /* DividerDefaults.Thickness */
                                        color = DividerDefaults.color
                                    ), shape = CardDefaults.outlinedShape
                                )
                        )
                        Row(Modifier.padding(thickness)) {
                            val draggableContainerSize = remember { mutableStateOf(Size.Zero) }
                            Column(modifier = Modifier.weight(1f)) {
                                keys.forEachIndexed { index, key ->

                                    val boxSize = remember { mutableStateOf(Size.Zero) }
                                    val cornerSizePx = cornerSize.toPx(boxSize.value, density)
                                    val singleCornerSize = cornerSizePx - dividerThickness

                                    val color = if (index < 3 && exactAnswer is Answer.Mtf) {
                                        val match = trueOptions[key] == exactAnswer.ans[index]
                                        if (match) Color(0x1600FF00) else Color(0x16FF0000)
                                    } else MaterialTheme.colorScheme.surfaceContainerLow
                                    val backgroundColor by animateColorAsState(
                                        targetValue = color,
                                        animationSpec = tween(animationDuration),
                                        label = "keyBoxBackgroundColor"
                                    )
                                    val topStart = if (index == 0) singleCornerSize else 0f
                                    val bottomStart =
                                        if (index == keys.lastIndex) singleCornerSize else 0f

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
                                        OptionText(
                                            key, modifier = Modifier.padding(horizontal = 16.dp)
                                        )
                                    }
                                    if (index != keys.lastIndex) HorizontalDivider(thickness = thickness)
                                }
                            }
                            VerticalDivider(thickness = thickness)
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .onSizeChanged { draggableContainerSize.value = it.toSize() }) {

                                val nList = List(values.size) { it }
                                val currentPosition = rememberSaveable(
                                    saver = listSaver(
                                        save = { it.toList() },
                                        restore = { it.toMutableStateList() })
                                ) { nList.toMutableStateList() }
                                var dragCount by rememberSaveable { mutableIntStateOf(0) }

                                values.forEachIndexed { index, value ->

                                    val currentIndex = currentPosition[index].takeIf { it != -1 }
                                        ?: return@forEachIndexed

                                    val xOffset = remember { Animatable(0f) }
                                    val yOffset = remember { Animatable(0f) }

                                    var isDragging by rememberSaveable { mutableStateOf(false) }
                                    var zIndex by rememberSaveable { mutableFloatStateOf(0f) }

                                    val boxSize = remember { mutableStateOf(Size.Zero) }
                                    val cornerSizePx = cornerSize.toPx(boxSize.value, density)
                                    val singleCornerSize = cornerSizePx - dividerThickness
                                    val halfDivider = dividerThickness / 2
                                    val boxWidth = boxSize.value.width
                                    val boxHeight = boxSize.value.height
                                    val xExtra = boxWidth / 8
                                    val yExtra = boxHeight / 3

                                    val parentBoxHeight = draggableContainerSize.value.height
                                    val dividerGap = dividerThickness * index
                                    val componentOffset = boxHeight * index + dividerGap
                                    val maxOffset = parentBoxHeight - boxHeight
                                    val singleSpace = boxHeight + dividerThickness

                                    val activeColor =
                                        MaterialTheme.colorScheme.surfaceContainerHighest
                                    val idleColor: Color =
                                        if (currentIndex < 3 && exactAnswer is Answer.Mtf) {
                                            val exactAnswer = exactAnswer.ans[currentIndex]
                                            val trueAnswer = trueValues[currentIndex]
                                            if (exactAnswer == trueAnswer) Color(0x1600FF00)
                                            else Color(0x16FF0000)
                                        } else MaterialTheme.colorScheme.surfaceContainerHigh
                                    val backgroundColor by animateColorAsState(
                                        targetValue = if (isDragging) activeColor else idleColor,
                                        animationSpec = tween(animationDuration),
                                        label = "boxBackgroundColor"
                                    )
                                    val topEnd by animateFloatAsState(
                                        targetValue = if (currentIndex == 0) singleCornerSize else 0f,
                                        animationSpec = tween(animationDuration),
                                        label = "boxTopEndShape"
                                    )
                                    val bottomEnd by animateFloatAsState(
                                        targetValue = if (currentIndex == values.lastIndex) singleCornerSize else 0f,
                                        animationSpec = tween(animationDuration),
                                        label = "boxBottomEndShape"
                                    )


                                    val diff = currentIndex - index
                                    val currentDividerGap = dividerThickness * diff
                                    val animateTo = boxHeight * diff + currentDividerGap

                                    LaunchedEffect(dragCount) {
                                        runCatching {
                                            yOffset.animateTo(animateTo, tween(animationDuration))
                                        }.onFailure { error ->
                                            error.printStackTrace()
                                        }
                                    }

                                    Box(
                                        contentAlignment = Alignment.CenterStart,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(64.dp)
                                            .offset {
                                                IntOffset(
                                                    xOffset.value.roundToInt(),
                                                    yOffset.value.roundToInt()
                                                )
                                            }
                                            .onSizeChanged { boxSize.value = it.toSize() }
                                            .background(
                                                color = backgroundColor, shape = RoundedCornerShape(
                                                    topEnd = topEnd, bottomEnd = bottomEnd
                                                )
                                            )
                                            .zIndex(zIndex)
                                            .pointerInput(Unit) {
                                                coroutineScope {
                                                    detectDragGestures(onDragStart = {
                                                        isDragging = true
                                                        zIndex = 1f
                                                    }, onDragEnd = {
                                                        launch {
                                                            listOf(async {
                                                                xOffset.animateTo(
                                                                    0f, tween(animationDuration)
                                                                )
                                                            }, async {
                                                                val currentOffset =
                                                                    yOffset.value + componentOffset + halfDivider
                                                                val middlePointer =
                                                                    currentOffset + (singleSpace / 2)

                                                                val i = currentPosition[index]
                                                                val j =
                                                                    if (singleSpace != 0f) middlePointer.roundToInt() / singleSpace.roundToInt()
                                                                    else i
                                                                Collections.swap(
                                                                    currentPosition, i, j
                                                                )
                                                                dragCount++
                                                            }).awaitAll()

                                                            isDragging = false
                                                            zIndex = 0f
                                                        }
                                                    }, onDrag = { change, dragAmount ->
                                                        launch {
                                                            change.consume()

                                                            // X Axis Logic
                                                            val currentX = xOffset.value
                                                            val xNormalized =
                                                                (currentX / xExtra).coerceIn(
                                                                    -1f, 1f
                                                                )
                                                            val xResistance =
                                                                1f - xNormalized.absoluteValue
                                                            val xDrag =
                                                                currentX + dragAmount.x * xResistance


                                                            // Y Axis Logic
                                                            val currentY = yOffset.value
                                                            val yFrom = 0f - componentOffset
                                                            val yTo = maxOffset - componentOffset

                                                            val belowMin =
                                                                (yFrom - currentY).coerceAtLeast(
                                                                    0f
                                                                )
                                                            val aboveMax =
                                                                (currentY - yTo).coerceAtLeast(
                                                                    0f
                                                                )
                                                            val overshoot = belowMin + aboveMax

                                                            val yNormalized =
                                                                (overshoot / yExtra).coerceIn(
                                                                    0f, 1f
                                                                )
                                                            val yResistance = 1f - yNormalized
                                                            val yDrag =
                                                                currentY + dragAmount.y * yResistance

                                                            val newX =
                                                                xDrag.coerceIn(-xExtra, xExtra)
                                                            val newY = yDrag.coerceIn(
                                                                -(yExtra + componentOffset),
                                                                (maxOffset - componentOffset) + yExtra
                                                            )


                                                            xOffset.snapTo(newX)
                                                            yOffset.snapTo(newY)
                                                        }
                                                    })
                                                }
                                            }) {
                                        OptionText(
                                            value, modifier = Modifier.padding(horizontal = 16.dp)
                                        )
                                    }
                                    if (index != values.lastIndex) HorizontalDivider(thickness = thickness)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OptionIcon(
    correct: Boolean, wrong: Boolean, modifier: Modifier = Modifier, default: @Composable () -> Unit
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
    style: TextStyle = MaterialTheme.typography.titleLarge
) {
    val keyText = text.uppercase()
    val adjustedText = when (keyText) {
        in caseNameStrings -> {
            runCatching { stringResource(enumValueOf<CaseName>(keyText).sktName) }.getOrDefault(
                keyText
            )
        }

        in genderNameStrings -> {
            runCatching { stringResource(enumValueOf<Gender>(keyText).skt) }.getOrDefault(keyText)
        }

        in formNameStrings -> {
            runCatching { stringResource(enumValueOf<FormName>(keyText).sktName) }.getOrDefault(
                keyText
            )
        }

        else -> keyText
    }
    Text(
        adjustedText, style = style, modifier = modifier.then(Modifier)
    )
}


@Composable
fun RegexText(
    @StringRes template: Int, key: Map<String, String>, modifier: Modifier = Modifier
) {
    val mutableKey = key.toMutableMap()
    val stringRes = stringResource(template)
    val vibaktiKey = "vibhakti"
    val vachanaKey = "vachana"
    if (mutableKey.containsKey(vibaktiKey) || mutableKey.containsKey(vachanaKey)) {
        val vibaktiValue = key.getValue(vibaktiKey)
        val vachanaValue = key.getValue(vachanaKey)
        val vibaktiEnum = CaseName.valueOf(vibaktiValue)
        val vachanaEnum = FormName.valueOf(vachanaValue)
        val vibaktiSktName = stringResource(vibaktiEnum.sktName)
        val vachanaSktName = stringResource(vachanaEnum.sktName)
        mutableKey[vibaktiKey] = vibaktiSktName
        mutableKey[vachanaKey] = vachanaSktName
    }
    val finalKey = mutableKey.toMap()
    val text = replacePlaceholders(stringRes, finalKey)
    Text(text, modifier = modifier, style = MaterialTheme.typography.titleLarge)
}


private fun replacePlaceholders(template: String, values: Map<String, String>): String {
    return template.replace(Regex("\\{(\\w+)\\}")) { match ->
        values[match.groupValues[1]] ?: match.value
    }
}

private val caseNameStrings = enumValues<CaseName>().map { it.name }.toSet()
private val genderNameStrings = enumValues<Gender>().map { it.name }.toSet()
private val formNameStrings = enumValues<FormName>().map { it.name }.toSet()