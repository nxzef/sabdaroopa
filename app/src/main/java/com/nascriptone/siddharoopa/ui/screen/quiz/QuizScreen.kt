package com.nascriptone.siddharoopa.ui.screen.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.data.model.Table
import com.nascriptone.siddharoopa.ui.screen.SiddharoopaRoutes
import com.nascriptone.siddharoopa.viewmodel.SiddharoopaViewModel
import kotlin.math.roundToInt

@Composable
fun QuizHomeScreen(
    navHostController: NavHostController,
    quizSectionState: QuizSectionState,
    modifier: Modifier = Modifier,
    viewModel: SiddharoopaViewModel,
) {
    val scrollState = rememberScrollState()

    val quizModes = QuizMode.entries
    val categoryOptions: List<Table?> = listOf(
        null, *Table.entries.toTypedArray()
    )

    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .verticalScroll(scrollState)
                .padding(horizontal = 8.dp)
        ) {

            IconButton(
                onClick = {
                    navHostController.navigate(SiddharoopaRoutes.QuizInstruction.name)
                }, modifier = Modifier
                    .align(Alignment.End)
                    .padding(vertical = 8.dp)
            ) {
                Icon(Icons.Outlined.Info, null)
            }

            QuizChooseOptionView(
                title = "Choose Category"
            ) {
                categoryOptions.forEachIndexed { index, table ->
                    val optionName = stringResource(table?.skt ?: R.string.all_category)
                    val subTitle = table?.subEng?.let { stringResource(it) }
                    QuizChooseOption(
                        optionName = optionName,
                        selected = quizSectionState.questionFrom == table,
                        onClick = { viewModel.updateQuizQuestionTable(table) },
                        optionSubTitle = subTitle
                    )
                    if (index < categoryOptions.lastIndex) {
                        HorizontalDivider()
                    }
                }
            }
            QuizChooseOptionView(
                title = "Question Type"
            ) {

                quizModes.forEachIndexed { index, type ->
                    val name = stringResource(type.uiName)
                    QuizChooseOption(
                        optionName = name,
                        selected = quizSectionState.quizMode == type,
                        onClick = { viewModel.updateQuizQuestionType(type) }
                    )
                    if (index < quizModes.lastIndex) {
                        HorizontalDivider()
                    }
                }

            }
            QuizChooseOptionView(
                title = "Question Range"
            ) {
                StepSlider(
                    viewModel = viewModel,
                    quizSectionState = quizSectionState
                )
            }
            Spacer(Modifier.height(28.dp))
            Button(
                onClick = {
                    navHostController.navigate(SiddharoopaRoutes.QuizQuestion.name)
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Begin Quiz")
            }
            Spacer(Modifier.height(48.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepSlider(
    viewModel: SiddharoopaViewModel,
    quizSectionState: QuizSectionState,
    modifier: Modifier = Modifier
) {

    var sliderPosition by rememberSaveable { mutableIntStateOf(quizSectionState.questionRange) }

    val sliderState = rememberSliderState(
        value = sliderPosition.toFloat(),
        steps = 2,
        onValueChangeFinished = {
            viewModel.updateQuizQuestionRange(sliderPosition)
        },
        valueRange = 5F..20F
    )
    sliderPosition = sliderState.value.roundToInt()

    Column(modifier.padding(horizontal = 8.dp, vertical = 16.dp)) {
        Text(
            "Questions: $sliderPosition",
            modifier = Modifier.padding(start = 14.dp),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp))
        Slider(
            state = sliderState,
            colors = SliderDefaults.colors(
                thumbColor = Color.Transparent
            ),
            thumb = {
                Box(
                    Modifier
                        .size(24.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            CircleShape
                        )
                )
            },
            track = {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                )
            }
        )
    }
}


@Composable
fun QuizChooseOptionView(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Column(
        modifier = modifier
            .padding(vertical = 20.dp, horizontal = 8.dp)
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(16.dp))
        OutlinedCard(
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            content()
        }
    }
}

@Composable
fun QuizChooseOption(
    optionName: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    optionSubTitle: String? = null
) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp)
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick,
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    optionName,
                    style = MaterialTheme.typography.titleMedium,
                )
                if (optionSubTitle != null) {
                    Text(
                        optionSubTitle,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.7F)
                    )
                }
            }
        }
    }
}

