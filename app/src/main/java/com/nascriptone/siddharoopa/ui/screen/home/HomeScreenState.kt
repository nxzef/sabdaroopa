package com.nascriptone.siddharoopa.ui.screen.home

data class HomeScreenState(
    val textFieldData: TextFieldData = TextFieldData()
)

data class TextFieldData(
    val text: String = "",
    val isSearchViewExpanded: Boolean = false
)