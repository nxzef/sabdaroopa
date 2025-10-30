package com.nxzef.sabdaroopa.data.model

import androidx.annotation.StringRes
import com.nxzef.sabdaroopa.R
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

typealias Declension = Map<CaseName, Map<FormName, String?>>

@Serializable
enum class CaseName(@param:StringRes val skt: Int) {
    @SerialName("nominative")
    NOMINATIVE(R.string.nominative),

    @SerialName("vocative")
    VOCATIVE(R.string.vocative),

    @SerialName("accusative")
    ACCUSATIVE(R.string.accusative),

    @SerialName("instrumental")
    INSTRUMENTAL(R.string.instrumental),

    @SerialName("dative")
    DATIVE(R.string.dative),

    @SerialName("ablative")
    ABLATIVE(R.string.ablative),

    @SerialName("genitive")
    GENITIVE(R.string.genitive),

    @SerialName("locative")
    LOCATIVE(R.string.locative)
}

@Serializable
enum class FormName(@param:StringRes val skt: Int) {
    @SerialName("single")
    SINGLE(R.string.single),

    @SerialName("dual")
    DUAL(R.string.dual),

    @SerialName("plural")
    PLURAL(R.string.plural)
}

@Serializable
enum class Category(@param:StringRes val skt: Int) {
    GENERAL(R.string.general_table),
    SPECIFIC(R.string.specific_table)
}

@Serializable
enum class Sound(@param:StringRes val skt: Int) {
    VOWELS(skt = R.string.vowel_skt),
    CONSONANTS(skt = R.string.consonant_skt),
}

@Serializable
enum class Gender(@param:StringRes val skt: Int) {
    MASCULINE(skt = R.string.masculine_skt),
    FEMININE(skt = R.string.feminine_skt),
    NEUTER(skt = R.string.neuter_skt)
}


