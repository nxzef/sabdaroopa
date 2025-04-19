package com.nascriptone.siddharoopa.ui.screen.category

import com.nascriptone.siddharoopa.data.model.uiobj.EntireSabda
import com.nascriptone.siddharoopa.data.model.uiobj.Gender
import com.nascriptone.siddharoopa.data.model.uiobj.Sound
import com.nascriptone.siddharoopa.data.model.uiobj.Table


data class CategoryScreenState(
    val selectedSabda: EntireSabda? = null,
    val selectedSound: Sound? = null,
    val selectedGender: Gender? = null,
    val lastFetchedTable: Table = Table.entries.first(),
    val isDataFetched: Boolean = false,
    val result: DataFetchState = DataFetchState.Loading,
    val filteredData: List<EntireSabda> = emptyList()
)

sealed class DataFetchState {
    data object Loading : DataFetchState()
    data class Error(val msg: String) : DataFetchState()
    data class Success(val data: List<EntireSabda>) : DataFetchState()
}

