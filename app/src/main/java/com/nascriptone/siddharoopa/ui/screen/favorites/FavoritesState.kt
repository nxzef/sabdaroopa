package com.nascriptone.siddharoopa.ui.screen.favorites

data class FavoritesState(
    val isSelectMode: Boolean = false,
    val trigger: Trigger = Trigger.NONE,
    val selectedIds: Set<Int> = emptySet(),
    val totalIds: Set<Int> = emptySet()
) {
    val areAllSelected: Boolean
        get() = selectedIds.isNotEmpty() && selectedIds.size == totalIds.size
}

enum class Trigger {
    NONE, AUTO, CARD, TOOLBAR
}