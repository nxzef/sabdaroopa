package com.nascriptone.siddharoopa.ui.screen.quiz

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.DefaultStrokeLineCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.navigation.NavHostController
import com.nascriptone.siddharoopa.data.model.uiobj.CaseName
import com.nascriptone.siddharoopa.data.model.uiobj.FormName
import com.nascriptone.siddharoopa.data.model.uiobj.Gender
import com.nascriptone.siddharoopa.ui.component.CurrentState
import com.nascriptone.siddharoopa.ui.screen.SiddharoopaRoutes
import com.nascriptone.siddharoopa.viewmodel.SiddharoopaViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val caseNameStrings = enumValues<CaseName>().map { it.name }.toSet()
private val genderNameStrings = enumValues<Gender>().map { it.name }.toSet()
private val formNameStrings = enumValues<FormName>().map { it.name }.toSet()


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
            viewModel,
            result.data,
            quizSectionState,
            navHostController,
            modifier
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

    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(24.dp))
            Text(
                "Question ${questionIndex + 1} of $questionCount",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(20.dp))
            LinearProgressIndicator(
                progress = { ((questionIndex + 1).toDouble() / questionCount).toFloat() },
                gapSize = 0.dp,
                strokeCap = DefaultStrokeLineCap,
                drawStopIndicator = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(16.dp)
                    .clip(CircleShape),
            )
            Box(
                modifier = Modifier
                    .weight(1F),
                contentAlignment = Alignment.Center
            ) {

                data.forEachIndexed { index, each ->
                    QuestionOption(
                        isVisible = index == questionIndex,
                        each = each,
                    )
                }

            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            if (questionIndex < (questionCount - 1)) {
                                questionIndex++
                            } else {
                                navHostController.navigate(SiddharoopaRoutes.QuizResult.name)
                            }
                        }
                    },
                    modifier = Modifier.weight(1F)
                ) {
                    Text("SKIP")
                }
                Spacer(Modifier.width(12.dp))
                Button(
                    enabled = true,
                    onClick = {
                        scope.launch {
                            if (questionIndex < questionCount) {

                                // Submit the answer
                                delay(1000)
                                if (questionIndex == lastQuestionIndex) {
                                    Log.d("list", "$data")
                                    navHostController.navigate(SiddharoopaRoutes.QuizResult.name)
                                } else {
                                    questionIndex++
                                }


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


@Composable
fun QuestionOption(
    isVisible: Boolean,
    each: QuestionOption,
    modifier: Modifier = Modifier
) {

    AnimatedVisibility(
        isVisible,
        enter = slideInHorizontally(
            initialOffsetX = { (it * 70) / 100 }
        ) + fadeIn(),
        exit = slideOutHorizontally(
            targetOffsetX = { -it / 4 }
        ) + fadeOut(),
        modifier = modifier
    ) {


        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
        ) {
            when (val state = each.option) {
                is Option.McqOption -> {
                    val options = state.data.options
                    RegexText(each.question, state.data.questionKey)
                    Spacer(Modifier.height(40.dp))
                    options.forEachIndexed { i, option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                .clickable { }
                                .padding(8.dp)
                        ) {
                            RadioButton(
                                selected = false,
                                onClick = { }
                            )
                            Spacer(Modifier.width(12.dp))
                            OptionText(
                                text = option,
                            )
                        }
                    }
                }

                is Option.MtfOption -> {
                    val options = state.data.options.toList()
                    RegexText(each.question, state.data.questionKey)
                    Spacer(Modifier.height(40.dp))
                    OutlinedCard {
                        options.fastForEachIndexed { index, (key, value) ->
                            Row(
                                modifier = Modifier
                                    .requiredHeight(64.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1F)
                                        .fillMaxSize(),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    OptionText(
                                        key,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                                VerticalDivider()
                                Box(
                                    modifier = Modifier
                                        .weight(1F)
                                        .fillMaxSize(),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    OptionText(
                                        value,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                            if (index != options.lastIndex) HorizontalDivider()
                        }
                    }
                }
            }
        }
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
        adjustedText,
        style = style,
        modifier = modifier.then(Modifier)
    )
}


@Composable
fun RegexText(
    @StringRes template: Int,
    key: Map<String, String>,
    modifier: Modifier = Modifier
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
    val text =
        replacePlaceholders(stringRes, finalKey)
    Text(text, modifier = modifier, style = MaterialTheme.typography.titleLarge)
}


private fun replacePlaceholders(template: String, values: Map<String, String>): String {
    return template.replace(Regex("\\{(\\w+)\\}")) { match ->
        values[match.groupValues[1]] ?: match.value
    }
}

