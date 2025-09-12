package com.nascriptone.siddharoopa.domain

import androidx.annotation.StringRes
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.data.model.Filter
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

    fun switchSource(source: Source) = _sourceWithData.update { source.createSourceData() }

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

    data class FromTable(val filter: Filter = Filter()) : SourceWithData {
        override val source: Source = Source.FROM_TABLE
    }

    data class FromFavorites(val data: Set<Int> = emptySet()) : SourceWithData {
        override val source: Source = Source.FROM_FAVORITES
    }

    data class FromList(val data: Set<Int> = emptySet()) : SourceWithData {
        override val source: Source = Source.FROM_LIST
    }
}

enum class Source(@StringRes val uiName: Int) {
    FROM_TABLE(uiName = R.string.pick_from_table), FROM_FAVORITES(uiName = R.string.pick_from_favorites), FROM_LIST(
        uiName = R.string.pick_from_list
    )
}

fun Source.createSourceData(): SourceWithData = when (this) {
    Source.FROM_TABLE -> SourceWithData.FromTable()
    Source.FROM_FAVORITES -> SourceWithData.FromFavorites()
    Source.FROM_LIST -> SourceWithData.FromList()
}