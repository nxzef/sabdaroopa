package com.nascriptone.siddharoopa.ui.screen.home

import com.nascriptone.siddharoopa.data.model.uiobj.EntireSabda

data class HomeScreenState(
    val result: ObserveSabda = ObserveSabda.Loading,
)

sealed class ObserveSabda {
    data class Error(val msg: String) : ObserveSabda()
    data object Loading : ObserveSabda()
    data class Success(val data: List<EntireSabda>) : ObserveSabda()
}