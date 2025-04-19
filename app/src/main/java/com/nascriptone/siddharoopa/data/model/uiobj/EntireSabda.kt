package com.nascriptone.siddharoopa.data.model.uiobj

import androidx.annotation.StringRes
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.data.model.entity.Sabda

data class EntireSabda(
    val sabda: Sabda,
    val table: Table
)

enum class Table(@StringRes val skt: Int) {
    GENERAL(R.string.general_table),
    SPECIFIC(R.string.specific_table)
}

enum class Sound(@StringRes val eng: Int, @StringRes val skt: Int) {
    CONSONANTS(eng = R.string.consonant_eng, skt = R.string.consonant_skt),
    VOWELS(eng = R.string.vowel_eng, skt = R.string.vowel_skt)
}

enum class Gender(@StringRes val eng: Int, @StringRes val skt: Int) {
    MASCULINE(eng = R.string.masculine_eng, skt = R.string.masculine_skt),
    FEMININE(eng = R.string.feminine_eng, skt = R.string.feminine_skt),
    NEUTER(eng = R.string.neuter_eng, skt = R.string.neuter_skt)
}


