package com.nascriptone.siddharoopa.ui.screen.quiz

import androidx.annotation.StringRes
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.data.model.uiobj.Table

data class QuizSectionState(
    val questionFrom: Table? = null,
    val quizMode: QuizMode = QuizMode.All,
    val questionRange: Int = 10,
    val currentAnswer: Answer = Answer.Unspecified,
    val questionList: CreationState<List<QuestionOption>> = CreationState.Loading,
    val result: ValuationState = ValuationState.Calculate
)

sealed class CreationState<out T> {
    object Loading : CreationState<Nothing>()
    data class Success<out T>(val data: T) : CreationState<T>()
    data class Error(val message: String) : CreationState<Nothing>()
}

enum class QuizMode(@StringRes val uiName: Int) {
    All(R.string.all_question_type),
    MCQ(R.string.multiple_choice_question),
    MTF(R.string.match_the_following)
}

data class QuestionOption(
    @StringRes val question: Int,
    val option: Option,
    val answer: Answer = Answer.Unspecified
)

sealed class Answer {
    data object Unspecified : Answer()
    data class Mcq(val mcqAns: String) : Answer()
    data class Mtf(val mtfAns: List<String>) : Answer()
}

sealed class Option {
    data class McqOption(val mcqData: McqGeneratedData) : Option()
    data class MtfOption(val mtfData: MtfGeneratedData) : Option()
}

data class McqGeneratedData(
    val options: Set<String>,
    val trueOption: String,
    val questionKey: Map<String, String>,
)

data class MtfGeneratedData(
    val options: Map<String, String>,
    val trueOption: Map<String, String>,
    val questionKey: Map<String, String>,
)

sealed class ValuationState {
    data object Calculate : ValuationState()
    data class Success(val result: Result) : ValuationState()
    data class Error(val message: String) : ValuationState()
}

data class Result(
    val dashboard: Dashboard
)

data class Dashboard(
    val mode: QuizMode,
    val accuracy: Float,
    val score: Int,
    val totalScore: Int
)