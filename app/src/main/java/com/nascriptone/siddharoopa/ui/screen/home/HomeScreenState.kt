package com.nascriptone.siddharoopa.ui.screen.home

data class HomeScreenState(
    val result: ObserveSabda = ObserveSabda.Loading,
)

sealed class ObserveSabda {
    data class Error(val msg: String) : ObserveSabda()
    data object Loading : ObserveSabda()
    data object Success : ObserveSabda()
}