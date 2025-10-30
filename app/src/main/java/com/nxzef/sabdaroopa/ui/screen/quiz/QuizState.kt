package com.nxzef.sabdaroopa.ui.screen.quiz

import androidx.annotation.StringRes
import com.nxzef.sabdaroopa.R
import com.nxzef.sabdaroopa.domain.DataSource

data class QuizSectionState(
    val dataSource: DataSource = DataSource.Table(),
    val mode: Mode = Mode.All,
    val range: Int = 10,
    val currentAnswer: Answer = Answer.Unspecified,
    val creationState: CreationState = CreationState.Loading,
    val valuationState: ValuationState = ValuationState.Calculate
)

sealed interface CreationState {
    object Loading : CreationState
    data class Success(val data: List<QuestionOption>) : CreationState
    data class Error(val message: String) : CreationState
}

enum class Action {
    SKIP,
    SUBMIT
}

enum class Mode(@param:StringRes val uiName: Int) {
    All(R.string.all_question_type),
    MCQ(R.string.multiple_choice_question),
    MTF(R.string.match_the_following)
}

data class QuestionOption(val questionWithNumber: QuestionWithNumber, val option: Option)

data class QuestionWithNumber(
    val number: Int,
    val question: String
)

sealed class Option {
    data class McqOption(val mcqGeneratedData: McqGeneratedData) : Option()
    data class MtfOption(val mtfGeneratedData: MtfGeneratedData) : Option()
}

data class McqGeneratedData(
    val options: Set<String>,
    val trueOption: String,
    val answer: String? = null
)

data class MtfGeneratedData(
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
    val finalData: List<QuestionOption>,
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
    @param:StringRes val message: Int = 0,
    val mode: Mode = Mode.All,
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

inline fun CreationState.requireSuccess(predicate: (List<QuestionOption>) -> Boolean): List<QuestionOption> {
    val success = this as? CreationState.Success ?: error("Not in Success state")
    require(predicate(success.data)) { "Predicate failed" }
    return success.data
}

