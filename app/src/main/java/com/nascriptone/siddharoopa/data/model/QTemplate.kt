package com.nascriptone.siddharoopa.data.model

import androidx.annotation.StringRes


enum class MCQ {
    ONE, TWO, THREE, FOUR, FIVE, SEVEN, EIGHT // Six removed because of a technical issue
}

enum class MTF {
    ONE, TWO, FOUR, FIVE // Three removed because of a technical issue
}

sealed class Phrase {
    data class McqKey(val type: MCQ) : Phrase()
    data class MtfKey(val type: MTF) : Phrase()
}


data class QTemplate(
    @StringRes val questionResId: Int,
    val phrase: Phrase
)