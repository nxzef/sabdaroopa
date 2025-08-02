package com.nascriptone.siddharoopa.ui.screen.category

import com.nascriptone.siddharoopa.data.model.EntireSabda
import com.nascriptone.siddharoopa.data.model.Gender
import com.nascriptone.siddharoopa.data.model.Sound
import com.nascriptone.siddharoopa.data.model.Table


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



