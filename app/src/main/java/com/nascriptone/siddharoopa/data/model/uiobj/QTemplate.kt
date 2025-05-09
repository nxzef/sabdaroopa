package com.nascriptone.siddharoopa.data.model.uiobj

import androidx.annotation.StringRes


enum class MCQ {
    ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE
}

enum class MTF {
    ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE
}

sealed class Phrase {
    data class McqKey(val mcqData: MCQ) : Phrase()
    data class MtfKey(val mtfData: MTF) : Phrase()
}




data class QTemplate(
    @StringRes val questionResId: Int,
    val phrase: Phrase
)