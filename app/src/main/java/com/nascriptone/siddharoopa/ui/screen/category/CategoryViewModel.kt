package com.nascriptone.siddharoopa.ui.screen.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nascriptone.siddharoopa.ui.state.Filter
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    repository: AppRepository,
) : ViewModel() {
    private val _filter = MutableStateFlow(Filter())

    @OptIn(ExperimentalCoroutinesApi::class)
    val list: Flow<PagingData<Sabda>> = _filter.flatMapLatest { filter ->
        repository.getFilteredList(filter).flow
    }.cachedIn(viewModelScope)

    fun updateFilter(filter: Filter) = _filter.update { filter }
}