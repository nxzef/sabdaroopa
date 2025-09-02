package com.nascriptone.siddharoopa.uscs

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedFavorites @Inject constructor() {
    private val _selectedIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedIds: StateFlow<Set<Int>> = _selectedIds.asStateFlow()

    fun updateSelectedSet(id: Int) = _selectedIds.update { it.toggleInSet(id) }

    fun clearSelectedSet() = _selectedIds.update { emptySet() }

    fun toggleSelectAll(ids: Set<Int>) {
        _selectedIds.update {
            if (it.size < ids.size) ids else emptySet()
        }
    }

    private fun <T> Set<T>.toggleInSet(i: T): Set<T> =
        if (i in this) this - i else this + i
}