package com.nascriptone.siddharoopa.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nascriptone.siddharoopa.data.repository.AppRepository
import com.nascriptone.siddharoopa.ui.screen.TableCategory
import com.nascriptone.siddharoopa.ui.screen.category.CategoryScreenState
import com.nascriptone.siddharoopa.ui.screen.category.DataFetchState
import com.nascriptone.siddharoopa.ui.screen.home.HomeScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SiddharoopaViewModel @Inject constructor(private val repository: AppRepository) :
    ViewModel() {

    private val _categoryUIState =
        MutableStateFlow(CategoryScreenState())
    val categoryUIState: StateFlow<CategoryScreenState> = _categoryUIState.asStateFlow()

    private val _homeUIState = MutableStateFlow(HomeScreenState())
    val homeUIState: StateFlow<HomeScreenState> = _homeUIState.asStateFlow()


    fun updateSelectedCategory(category: TableCategory, title: String) {
        _homeUIState.update {
            it.copy(
                selectedCategory = category,
                categoryTitle = title
            )
        }
    }


    fun fetchSabda() {
        val title = homeUIState.value.categoryTitle
        val category = homeUIState.value.selectedCategory

        _categoryUIState.update {
            it.copy(screenTitle = title, result = DataFetchState.Loading)
        }

        viewModelScope.launch {
            val result = try {
                DataFetchState.Success(
                    data = when (category) {
                        TableCategory.General -> repository.getAllGeneralSabda()
                        TableCategory.Specific -> repository.getAllSpecificSabda()
                    }
                )
            } catch (e: Exception) {
                DataFetchState.Error(e.message.toString())
            }

            _categoryUIState.update {
                it.copy(screenTitle = title, result = result)
            }
        }
    }


}