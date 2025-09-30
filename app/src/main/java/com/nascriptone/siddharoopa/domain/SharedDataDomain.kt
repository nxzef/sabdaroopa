package com.nascriptone.siddharoopa.domain

import androidx.annotation.StringRes
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.ui.state.Filter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedDataDomain @Inject constructor() {
    private val _sourceWithData = MutableStateFlow<SourceWithData>(SourceWithData.FromTable())
    val sourceWithData: StateFlow<SourceWithData> = _sourceWithData.asStateFlow()

    fun updateSourceWithData(sourceWithData: SourceWithData) =
        _sourceWithData.update { sourceWithData }


    fun updateFilter(filter: Filter) = _sourceWithData.update { state ->
        when (state) {
            is SourceWithData.FromTable -> state.copy(filter)
            else -> state
        }
    }

    fun resetSource() = _sourceWithData.update { SourceWithData.FromTable() }
}

sealed interface SourceWithData {
    val source: Source
    val data: Set<Int> get() =  emptySet()
    val display: String get() = ""
    data class FromTable(val filter: Filter = Filter()) : SourceWithData {
        override val source: Source = Source.FROM_TABLE
    }

    data class FromFavorites(
        override val data: Set<Int> = emptySet(),
        override val display: String = ""
    ) : SourceWithData {
        override val source: Source = Source.FROM_FAVORITES
        fun hasChanged(selectedIds: Set<Int>): Boolean = selectedIds != data
    }

    data class FromList(
        override val data: Set<Int> = emptySet(),
        override val display: String = ""
    ) : SourceWithData {
        override val source: Source = Source.FROM_LIST
    }
}

enum class Source(@StringRes val uiName: Int) {
    FROM_TABLE(uiName = R.string.pick_from_table), FROM_FAVORITES(uiName = R.string.pick_from_favorites), FROM_LIST(
        uiName = R.string.pick_from_list
    );

    fun createSourceData(): SourceWithData = when (this) {
        FROM_TABLE -> SourceWithData.FromTable()
        FROM_FAVORITES -> SourceWithData.FromFavorites()
        FROM_LIST -> SourceWithData.FromList()
    }
}