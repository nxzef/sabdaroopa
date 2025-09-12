package com.nascriptone.siddharoopa.ui.screen.favorites

data class FavoritesState(
    val fromQuiz: Boolean = false,
    val isSelectMode: Boolean = false,
    val selectionTrigger: SelectionTrigger = SelectionTrigger.EXPLICIT,
    val selectedIds: Set<Int> = emptySet(),
    val totalIds: Set<Int> = emptySet()
) {
    val areAllSelected: Boolean
        get() = selectedIds.isNotEmpty() && selectedIds.size == totalIds.size
}

enum class SelectionTrigger {
    EXPLICIT, CARD, TOOLBAR
}