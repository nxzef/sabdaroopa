package com.nascriptone.siddharoopa.data.local

import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.data.model.uiobj.MCQ
import com.nascriptone.siddharoopa.data.model.uiobj.MTF
import com.nascriptone.siddharoopa.data.model.uiobj.Phrase
import com.nascriptone.siddharoopa.data.model.uiobj.QTemplate

object QuizQuestion {
    val mcqQuestions = listOf<QTemplate>(
        QTemplate(R.string.mcq_template_1, Phrase.McqKey(MCQ.ONE)),
        QTemplate(R.string.mcq_template_2, Phrase.McqKey(MCQ.TWO)),
        QTemplate(R.string.mcq_template_3, Phrase.McqKey(MCQ.THREE)),
        QTemplate(R.string.mcq_template_4, Phrase.McqKey(MCQ.FOUR)),
        QTemplate(R.string.mcq_template_5, Phrase.McqKey(MCQ.FIVE)),
        QTemplate(R.string.mcq_template_6, Phrase.McqKey(MCQ.SIX)),
        QTemplate(R.string.mcq_template_7, Phrase.McqKey(MCQ.SEVEN)),
        QTemplate(R.string.mcq_template_8, Phrase.McqKey(MCQ.EIGHT)),
    )
    val mtfQuestions = listOf(
        QTemplate(R.string.mtf_template_1, Phrase.MtfKey(MTF.ONE)),
        QTemplate(R.string.mtf_template_2, Phrase.MtfKey(MTF.TWO)),
        QTemplate(R.string.mtf_template_3, Phrase.MtfKey(MTF.THREE)),
        QTemplate(R.string.mtf_template_4, Phrase.MtfKey(MTF.FOUR)),
        QTemplate(R.string.mtf_template_5, Phrase.MtfKey(MTF.FIVE)),
        QTemplate(R.string.mtf_template_6, Phrase.MtfKey(MTF.SIX)),
        QTemplate(R.string.mtf_template_7, Phrase.MtfKey(MTF.SEVEN)),
        QTemplate(R.string.mtf_template_8, Phrase.MtfKey(MTF.EIGHT)),
        QTemplate(R.string.mtf_template_9, Phrase.MtfKey(MTF.NINE)),
    )
}