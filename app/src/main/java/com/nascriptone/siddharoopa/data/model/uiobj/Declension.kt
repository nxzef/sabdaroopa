package com.nascriptone.siddharoopa.data.model.uiobj

import kotlinx.serialization.Serializable

@Serializable
data class Declension(
    val nominative: CaseForm?,
    val vocative: CaseForm?,
    val accusative: CaseForm?,
    val instrumental: CaseForm?,
    val dative: CaseForm?,
    val ablative: CaseForm?,
    val genitive: CaseForm?,
    val locative: CaseForm?
)


@Serializable
data class CaseForm(
    val single: String?,
    val dual: String?,
    val plural: String?
)

