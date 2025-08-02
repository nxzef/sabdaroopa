package com.nascriptone.siddharoopa.data.model

import androidx.annotation.StringRes


enum class MCQ {
    ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT
}

enum class MTF {
    ONE, TWO, THREE, FOUR, FIVE
}

sealed class Phrase {
    data class McqKey(val type: MCQ) : Phrase()
    data class MtfKey(val type: MTF) : Phrase()
}


data class QTemplate(
    @StringRes val questionResId: Int,
    val phrase: Phrase
)