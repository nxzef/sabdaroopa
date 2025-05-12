package com.nascriptone.siddharoopa.ui.screen.quiz

import androidx.annotation.StringRes
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.data.model.uiobj.Table

data class QuizSectionState(
    val questionFrom: Table? = null,
    val questionType: QuestionType = QuestionType.All,
    val questionRange: Float = 10F,
    val result: CreationState = CreationState.Loading
)

enum class QuestionType(@StringRes val uiName: Int) {
    All(R.string.all_question_type),
    MCQ(R.string.multiple_choice_question),
    MTF(R.string.match_the_following)
}


sealed class Option {
    data class McqOption(val data: McqGeneratedData) : Option()
    data class MtfOption(val data: MtfGeneratedData) : Option()
}

data class QuestionOption(
    val question: Int,
    val option: Option
)

data class McqGeneratedData(
    val options: Set<String>,
    val trueOption: String,
    val questionKey: Map<String, String>
)

data class MtfGeneratedData(
    val options: Map<String, String>,
    val questionKey: Map<String, String>
)


sealed class CreationState {
    data object Loading : CreationState()
    data class Error(val msg: String) : CreationState()
    data class Success(val data: List<QuestionOption>) : CreationState()
}
