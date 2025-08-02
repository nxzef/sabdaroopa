package com.nascriptone.siddharoopa.data.model

import androidx.annotation.StringRes
import com.nascriptone.siddharoopa.R
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

typealias Declension = Map<CaseName, Map<FormName, String?>>

@Serializable
enum class CaseName(@StringRes val sktName: Int) {
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
enum class FormName(@StringRes val sktName: Int) {
    @SerialName("single")
    SINGLE(R.string.single),

    @SerialName("dual")
    DUAL(R.string.dual),

    @SerialName("plural")
    PLURAL(R.string.plural)
}


