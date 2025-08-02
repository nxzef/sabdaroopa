package com.nascriptone.siddharoopa.ui.screen.quiz

import androidx.annotation.StringRes
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.data.model.Table

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

enum class Action {
    SKIP,
    SUBMIT
}

enum class QuizMode(@StringRes val uiName: Int) {
    All(R.string.all_question_type),
    MCQ(R.string.multiple_choice_question),
    MTF(R.string.match_the_following)
}

data class QuestionOption(val state: State)

sealed class State {
    data class McqState(val data: McqGeneratedData) : State()
    data class MtfState(val data: MtfGeneratedData) : State()
}

data class McqGeneratedData(
    @StringRes val template: Int,
    val templateKey: Map<String, String>,
    val options: Set<String>,
    val trueOption: String,
    val answer: String? = null
)

data class MtfGeneratedData(
    @StringRes val template: Int,
    val templateKey: Map<String, String>,
    val options: Map<String?, String>,
    val trueOption: List<String>,
    val answer: List<String>? = null
)

sealed interface Answer {
    data object Unspecified : Answer
    data class Mcq(val answer: String) : Answer
    data class Mtf(val answer: List<String>) : Answer
}

sealed interface ValuationState {
    data object Calculate : ValuationState
    data class Success(val result: Result) : ValuationState
    data class Error(val message: String) : ValuationState
}

data class Result(
    val mcqStats: McqStats? = null,
    val mtfStats: MtfStats? = null,
    val dashboard: Dashboard
) {

    val summary: Summary?
        get() = Summary(
            totalQuestions = (mcqStats?.totalQuestions ?: 0) + (mtfStats?.totalSet ?: 0),
            totalPossibleScore = dashboard.totalPossibleScore,
            score = dashboard.score,
            accuracy = dashboard.accuracy
        ).takeIf { mcqStats != null && mtfStats != null }

}

data class Dashboard(
    val accuracy: Float = 0f,
    @StringRes val message: Int = 0,
    val table: Table? = null,
    val score: Int = 0,
    val totalPossibleScore: Int = 0,
)

data class McqStats(
    val totalQuestions: Int,
    val attended: Int,
    val skipped: Int,
    val correct: Int,
    val wrong: Int,
    val score: Int
)

data class MtfStats(
    val totalSet: Int,
    val totalPairs: Int,
    val attended: Int,
    val skipped: Int,
    val correct: Int,
    val wrong: Int,
    val score: Int
)

data class Summary(
    val totalQuestions: Int,
    val totalPossibleScore: Int,
    val score: Int,
    val accuracy: Float
)