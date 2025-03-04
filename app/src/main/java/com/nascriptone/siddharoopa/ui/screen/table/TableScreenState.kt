package com.nascriptone.siddharoopa.ui.screen.table

data class TableScreenState(
    val title: String = "",
    val selectedTable: String = "",
    val result: StringParse = StringParse.Loading
)

sealed class StringParse {
    data object Loading : StringParse()
    data class Success(val declensionTable: List<List<String>>) : StringParse()
    data class Error(val msg: String) : StringParse()
}
