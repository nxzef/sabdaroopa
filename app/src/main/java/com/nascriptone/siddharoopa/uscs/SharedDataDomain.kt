package com.nascriptone.siddharoopa.uscs

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedFavorites @Inject constructor() {
    private val _selectedSet = MutableStateFlow(SelectedSet())
    val selectedSet: StateFlow<SelectedSet> = _selectedSet.asStateFlow()

    fun updateDataSource(dataSource: DataSource) = _selectedSet.update {
        it.copy(dataSource = dataSource)
    }

    fun updateSelectedSet(id: Int) = _selectedSet.update {
        it.copy(
            data = it.data.toggleInSet(id)
        )
    }

    fun clearSelectedSet() = _selectedSet.update {
        it.copy(
            dataSource = DataSource.None,
            data = emptySet()
        )
    }

    fun toggleSelectAll(ids: Set<Int>) {
        _selectedSet.update {
            it.copy(
                data = if (it.data.size < ids.size) ids else emptySet()
            )
        }
    }

    private fun <T> Set<T>.toggleInSet(i: T): Set<T> =
        if (i in this) this - i else this + i
}

enum class DataSource {
    None,
    Favorites,
    List
}

data class SelectedSet(
    val dataSource: DataSource = DataSource.None,
    val data: Set<Int> = emptySet()
)