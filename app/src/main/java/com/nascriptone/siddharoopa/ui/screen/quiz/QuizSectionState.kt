package com.nascriptone.siddharoopa.ui.screen.quiz

import androidx.annotation.StringRes
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.data.model.uiobj.Table

data class QuizSectionState(
    val questionFrom: Table? = null,
    val questionType: QuestionType = QuestionType.All,
    val questionRange: Float = 10F
)

enum class QuestionType(@StringRes val uiName: Int) {
    All(R.string.all_question_type),
    MCQ(R.string.multiple_choice_question),
    FTB(R.string.fill_in_the_blanks),
    MTF(R.string.match_the_following)
}
