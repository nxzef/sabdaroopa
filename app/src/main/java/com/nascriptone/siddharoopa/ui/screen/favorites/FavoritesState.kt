package com.nascriptone.siddharoopa.ui.screen.favorites

data class FavoritesState(
    val isSelectMode: Boolean = false,
    val selectionTrigger: SelectionTrigger = SelectionTrigger.EXPLICIT,
    val selectedIds: Set<Int> = emptySet(),
    val areAllSelected: Boolean = false
)

enum class SelectionTrigger {
    EXPLICIT, CARD, TOOLBAR
}