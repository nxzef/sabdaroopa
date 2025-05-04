package com.nascriptone.siddharoopa.data.model.uiobj

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//@Serializable
//data class Declension(
//    val cases: Map<CaseName, Map<FormName, String?>>
//)


typealias Declension = Map<CaseName, Map<FormName, String?>>

//@Serializable
//data class CaseForm(
//    val forms: Map<FormName, String?>
//)

@Serializable
enum class CaseName {
    @SerialName("nominative")
    NOMINATIVE,

    @SerialName("vocative")
    VOCATIVE,

    @SerialName("accusative")
    ACCUSATIVE,

    @SerialName("instrumental")
    INSTRUMENTAL,

    @SerialName("dative")
    DATIVE,

    @SerialName("ablative")
    ABLATIVE,

    @SerialName("genitive")
    GENITIVE,

    @SerialName("locative")
    LOCATIVE
}

@Serializable
enum class FormName {
    @SerialName("single")
    SINGLE,

    @SerialName("dual")
    DUAL,

    @SerialName("plural")
    PLURAL
}


