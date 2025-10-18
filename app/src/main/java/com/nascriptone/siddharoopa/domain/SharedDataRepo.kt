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
class SharedDataRepo @Inject constructor() {
    private val _dataSource = MutableStateFlow<DataSource>(DataSource.Table())
    val dataSource: StateFlow<DataSource> = _dataSource.asStateFlow()

    fun updateDataSource(dataSource: DataSource) =
        _dataSource.update { dataSource }

    fun resetToTable() = _dataSource.update { DataSource.Table() }
}

sealed class DataSource {

    abstract val type: SourceType
    open val ids: Set<Int> = emptySet()
    open val display: String = ""
    abstract fun hasValidData(): Boolean
    data class Table(val filter: Filter = Filter()) : DataSource() {
        override val type: SourceType = SourceType.TABLE
        override fun hasValidData(): Boolean {
            return filter.category != null || filter.gender != null || filter.sound != null
        }
    }

    data class Favorites(
        override val ids: Set<Int> = emptySet(),
        override val display: String = ""
    ) : DataSource() {
        override val type: SourceType = SourceType.FAVORITES
        override fun hasValidData(): Boolean = ids.isNotEmpty()
        fun hasChanged(selectedIds: Set<Int>): Boolean = selectedIds != ids
    }

    data class CustomList(
        override val ids: Set<Int> = emptySet(),
        override val display: String = ""
    ) : DataSource() {
        override val type: SourceType = SourceType.CUSTOM_LIST
        override fun hasValidData(): Boolean = ids.isNotEmpty()
        fun hasChanged(selectedIds: Set<Int>): Boolean = selectedIds != ids
    }
}

enum class SourceType(@param:StringRes val labelResId: Int) {
    TABLE(labelResId = R.string.pick_from_table),
    FAVORITES(labelResId = R.string.pick_from_favorites),
    CUSTOM_LIST(labelResId = R.string.pick_from_list);

    fun createDefault(): DataSource = when (this) {
        TABLE -> DataSource.Table()
        FAVORITES -> DataSource.Favorites()
        CUSTOM_LIST -> DataSource.CustomList()
    }
}