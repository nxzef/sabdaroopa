package com.nascriptone.siddharoopa.data.model.uiobj

import androidx.annotation.StringRes

data class QTemplate(
    @StringRes val templateId: Int,
    val phrase: LogicKey = LogicKey.Mcq.CaseFormSabda
)

sealed interface LogicKey {

    sealed interface Mcq : LogicKey {
        object CaseFormSabda : Mcq
        object GenderPick : Mcq
        object VachanaPick : Mcq
        object SabdaCategory : Mcq
        object SabdaMeaning : Mcq
    }

    sealed interface Mtf : LogicKey {
        object VibhaktiMatch : Mtf
        object FormMatch : Mtf
        object VachanaMatch : Mtf
        object GenderMatch : Mtf
        object WordTranslationMatch : Mtf
    }

}

