package com.nascriptone.siddharoopa.ui.screen.table

import com.nascriptone.siddharoopa.data.model.uiobj.EntireSabda

data class TableScreenState(
    val currentSabda: EntireSabda? = null,
    val isItFavorite: Boolean = false,
    val result: StringParse = StringParse.Loading
)

sealed class StringParse {
    data object Loading : StringParse()
    data class Success(val declensionTable: List<List<String?>>) : StringParse()
    data class Error(val msg: String) : StringParse()
}
