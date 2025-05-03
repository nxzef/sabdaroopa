package com.nascriptone.siddharoopa.data.local

import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.data.model.uiobj.LogicKey.Mcq
import com.nascriptone.siddharoopa.data.model.uiobj.LogicKey.Mtf
import com.nascriptone.siddharoopa.data.model.uiobj.QTemplate

object QuizQuestion {
    val mcqQuestions = listOf(
        QTemplate(R.string.mcq_template_1, Mcq.CaseFormSabda),
        QTemplate(R.string.mcq_template_2, Mcq.CaseFormSabda),
        QTemplate(R.string.mcq_template_3, Mcq.CaseFormSabda),
        QTemplate(R.string.mcq_template_4, Mcq.GenderPick),
        QTemplate(R.string.mcq_template_5, Mcq.VachanaPick),
        QTemplate(R.string.mcq_template_6, Mcq.SabdaCategory),
        QTemplate(R.string.mcq_template_7, Mcq.SabdaMeaning),
        QTemplate(R.string.mcq_template_8, Mcq.CaseFormSabda),
        QTemplate(R.string.mcq_template_9, Mcq.CaseFormSabda),
        QTemplate(R.string.mcq_template_10, Mcq.CaseFormSabda)
    )
    val mtfQuestions = listOf(
        QTemplate(R.string.mtf_template_1, Mtf.VibhaktiMatch),
        QTemplate(R.string.mtf_template_2, Mtf.FormMatch),
        QTemplate(R.string.mtf_template_3, Mtf.VachanaMatch),
        QTemplate(R.string.mtf_template_4, Mtf.WordTranslationMatch),
        QTemplate(R.string.mtf_template_5, Mtf.VibhaktiMatch),
        QTemplate(R.string.mtf_template_6, Mtf.GenderMatch),
        QTemplate(R.string.mtf_template_7, Mtf.FormMatch),
        QTemplate(R.string.mtf_template_8, Mtf.VachanaMatch),
        QTemplate(R.string.mtf_template_9, Mtf.VibhaktiMatch),
        QTemplate(R.string.mtf_template_10, Mtf.FormMatch)
    )
}