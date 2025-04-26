package com.nascriptone.siddharoopa.ui.screen.category

import com.nascriptone.siddharoopa.data.model.uiobj.EntireSabda
import com.nascriptone.siddharoopa.data.model.uiobj.Gender
import com.nascriptone.siddharoopa.data.model.uiobj.Sound
import com.nascriptone.siddharoopa.data.model.uiobj.Table


data class CategoryScreenState(
    val selectedTable: Table? = null,
    val selectedSound: Sound? = null,
    val selectedGender: Gender? = null,
    val result: FilterState = FilterState.Loading
)

sealed class FilterState {
    data object Loading : FilterState()
    data class Error(val msg: String) : FilterState()
    data class Success(val filteredData: List<EntireSabda>) : FilterState()
}



