package com.nascriptone.siddharoopa.ui.screen.category

import com.nascriptone.siddharoopa.data.model.entity.Sabda


data class CategoryScreenState(
    val screenTitle: String = "",
    val selectedTable: String = "",
    val result: DataFetchState = DataFetchState.Loading
)

sealed class DataFetchState {
    data object Loading : DataFetchState()
    data class Error(val msg: String) : DataFetchState()
    data class Success(val data: List<Sabda>) : DataFetchState()
}
