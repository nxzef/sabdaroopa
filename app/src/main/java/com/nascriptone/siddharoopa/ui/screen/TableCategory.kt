package com.nascriptone.siddharoopa.ui.screen

import androidx.annotation.StringRes
import com.nascriptone.siddharoopa.R

enum class TableCategory {
    General,
    Specific
}

enum class Gender {
    Masculine,
    Feminine,
    Neuter
}

enum class Sound(@StringRes val eng: Int, @StringRes val skt: Int) {
    CONSONANTS(eng = R.string.consonant_eng, skt = R.string.consonant_skt),
    VOWELS(eng = R.string.vowel_eng, skt = R.string.vowel_skt)
}