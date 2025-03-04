package com.nascriptone.siddharoopa.ui.screen.category

import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.data.model.uiobj.CategoryViewType
import com.nascriptone.siddharoopa.data.model.uiobj.Sound


data class CategoryScreenState(
    val selectedCategory: CategoryViewType? = null,
    val selectedSound: Sound? = null,
    val isDataFetched: Boolean = false,
    val result: DataFetchState = DataFetchState.Loading
)

sealed class DataFetchState {
    data object Loading : DataFetchState()
    data class Error(val msg: String) : DataFetchState()
    data class Success(val data: List<Sabda>) : DataFetchState()
}
