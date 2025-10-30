package com.nxzef.sabdaroopa.data.local

import com.nxzef.sabdaroopa.R
import com.nxzef.sabdaroopa.data.model.MCQ
import com.nxzef.sabdaroopa.data.model.MTF
import com.nxzef.sabdaroopa.data.model.Phrase
import com.nxzef.sabdaroopa.data.model.QTemplate

object QuizQuestion {
    val mcqQuestions = listOf(
        QTemplate(R.string.mcq_template_1, Phrase.McqKey(MCQ.ONE)),
        QTemplate(R.string.mcq_template_2, Phrase.McqKey(MCQ.TWO)),
        QTemplate(R.string.mcq_template_3, Phrase.McqKey(MCQ.THREE)),
        QTemplate(R.string.mcq_template_4, Phrase.McqKey(MCQ.FOUR)),
        QTemplate(R.string.mcq_template_5, Phrase.McqKey(MCQ.FIVE)),
        QTemplate(R.string.mcq_template_7, Phrase.McqKey(MCQ.SEVEN)),
        QTemplate(R.string.mcq_template_8, Phrase.McqKey(MCQ.EIGHT)),
    )
    val mtfQuestions = listOf(
        QTemplate(R.string.mtf_template_1, Phrase.MtfKey(MTF.ONE)),
        QTemplate(R.string.mtf_template_2, Phrase.MtfKey(MTF.TWO)),
        QTemplate(R.string.mtf_template_4, Phrase.MtfKey(MTF.FOUR)),
        QTemplate(R.string.mtf_template_5, Phrase.MtfKey(MTF.FIVE)),
    )
}