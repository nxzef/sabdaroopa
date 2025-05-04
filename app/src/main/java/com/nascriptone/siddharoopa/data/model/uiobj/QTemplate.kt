package com.nascriptone.siddharoopa.data.model.uiobj

import androidx.annotation.StringRes


enum class MCQ {
    ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN
}

enum class MTF {
    ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN
}

sealed class Phrase {
    data class McqKey(val data: MCQ) : Phrase()
    data class MtfKey(val data: MTF) : Phrase()
}




data class QTemplate(
    @StringRes val questionResId: Int,
    val phrase: Phrase
)