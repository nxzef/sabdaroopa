package com.nascriptone.siddharoopa.ui.screen.favorites

data class FavoritesState(
    val isSelectMode: Boolean = false,
    val selectionTrigger: SelectionTrigger = SelectionTrigger.EXPLICIT,
    val selectedIds: Set<Int> = emptySet(),
    private val totalIDs: Set<Int> = emptySet()
) {
    val areAllSelected: Boolean
        get() = selectedIds.isNotEmpty() && selectedIds.size == totalIDs.size
}

enum class SelectionTrigger {
    EXPLICIT, CARD, TOOLBAR
}