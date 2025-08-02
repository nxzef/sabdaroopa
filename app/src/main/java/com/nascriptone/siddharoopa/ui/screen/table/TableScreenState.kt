package com.nascriptone.siddharoopa.ui.screen.table

import com.nascriptone.siddharoopa.data.model.Declension
import com.nascriptone.siddharoopa.data.model.EntireSabda

data class TableScreenState(
    val selectedSabda: EntireSabda? = null,
    val result: StringParse = StringParse.Loading
)

sealed class StringParse {
    data object Loading : StringParse()
    data class Success(val declension: Declension) : StringParse()
    data class Error(val msg: String) : StringParse()
}
