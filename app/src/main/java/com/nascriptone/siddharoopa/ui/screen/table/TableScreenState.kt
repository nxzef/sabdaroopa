package com.nascriptone.siddharoopa.ui.screen.table

import com.nascriptone.siddharoopa.data.model.entity.FavoriteSabda
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.ui.screen.TableCategory

data class TableScreenState(
    val selectedSabda: UserSelectedSabda = UserSelectedSabda(),
    val currentSabda: FavoriteSabda = FavoriteSabda(
        favSabdaId = 0,
        favSabdaCategory = ""
    ),
    val isItFavorite: Boolean = false,
    val result: StringParse = StringParse.Loading
)

sealed class StringParse {
    data object Loading : StringParse()
    data class Success(val declensionTable: List<List<String?>>) : StringParse()
    data class Error(val msg: String) : StringParse()
}

data class UserSelectedSabda(
    val sabda: Sabda? = null,
    val tableCategory: TableCategory? = null,
    val sabdaDetailText: String = ""
)
