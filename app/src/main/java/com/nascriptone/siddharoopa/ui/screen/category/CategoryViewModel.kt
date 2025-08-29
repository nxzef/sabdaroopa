package com.nascriptone.siddharoopa.ui.screen.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nascriptone.siddharoopa.data.model.Gender
import com.nascriptone.siddharoopa.data.model.Sound
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(repository: AppRepository) : ViewModel() {
    private val _sabdaList = repository.getAllSabda().distinctUntilChanged().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )
    private val _filter = MutableStateFlow(Filter())
    val uiState: StateFlow<CategoryState> = combine(
        _sabdaList, _filter
    ) { sabdaList, filter ->
        val filterState = try {
            val data = sabdaList.applyFilter(filter)
            FilterState.Success(data)
        } catch (e: Exception) {
            FilterState.Error(e.message ?: "Unknown error occurred")
        }
        CategoryState(filter = filter, filterState = filterState)
    }.distinctUntilChanged().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CategoryState()
    )

//    @OptIn(ExperimentalCoroutinesApi::class)
//    val uiState: StateFlow<CategoryState> = combine(
//        _sabdaList, _filter
//    ) { sabdaList, filter ->
//        filter to sabdaList
//    }.flatMapLatest { (filter, sabdaList) ->
//        flow {
//            emit(CategoryState(filter = filter, filterState = FilterState.Loading))
//
//            try {
//                val filteredData = withContext(Dispatchers.Default) {
//                    sabdaList.applyFilter(filter)
//                }
//
//                emit(CategoryState(filter = filter, filterState = FilterState.Success(filteredData)))
//            } catch (e: Exception) {
//                emit(CategoryState(filter = filter, filterState = FilterState.Error(e.message ?: "Unknown error occurred")))
//            }
//        }
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5_000),
//        initialValue = CategoryState()
//    )


    fun updateGenderFilter(gender: Gender?) {
        _filter.update {
            it.copy(selectedGender = gender)
        }
    }

    fun updateSoundFilter(sound: Sound) {
        _filter.update {
            it.copy(selectedSound = sound)
        }
    }

    fun initializeFilter(filter: Filter) = _filter.update { filter }
}

private fun List<Sabda>.applyFilter(filter: Filter): List<Sabda> {
    return this.filter { sabda ->
        (filter.selectedCategory == null || filter.selectedCategory == sabda.category) &&
                (filter.selectedSound == null || filter.selectedSound == sabda.sound) &&
                (filter.selectedGender == null || filter.selectedGender == sabda.gender)
    }
}