package com.nascriptone.siddharoopa.ui.screen.favorites

import com.nascriptone.siddharoopa.ui.state.DataTransState

data class FavoritesState(
    val isSelectMode: Boolean = false,
    val trigger: Trigger = Trigger.NONE,
    val selectedIds: Set<Int> = emptySet(),
    val totalIds: Set<Int> = emptySet(),
    val dataTransState: DataTransState = DataTransState.None
) {
    val areAllSelected: Boolean
        get() = selectedIds.isNotEmpty() && selectedIds.size == totalIds.size
}

enum class Trigger {
    NONE, AUTO, CARD, TOOLBAR
}