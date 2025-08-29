package com.nascriptone.siddharoopa.ui.screen.favorites

import com.nascriptone.siddharoopa.data.model.entity.Sabda

data class FavoritesState(
    val isSelectMode: Boolean = false,
    val selectedIds: Set<Int> = emptySet(),
    val selectionTrigger: SelectionTrigger = SelectionTrigger.NONE,
    val gatherState: GatherState<List<Sabda>> = GatherState.Loading
)

val FavoritesState.areAllSelected: Boolean
    get() {
        val list = (gatherState as? GatherState.Success<List<Sabda>>)?.favoriteList.orEmpty()
        return list.isNotEmpty() && selectedIds.size == list.size
    }

sealed interface GatherState<out T> {
    data object Empty : GatherState<Nothing>
    data object Loading : GatherState<Nothing>
    data class Success<T>(val favoriteList: T) : GatherState<T>
    data class Error(val message: String) : GatherState<Nothing>
}

enum class SelectionTrigger {
    NONE, CARD, TOOLBAR
}