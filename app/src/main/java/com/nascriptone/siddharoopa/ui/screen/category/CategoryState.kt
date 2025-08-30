package com.nascriptone.siddharoopa.ui.screen.category

import com.nascriptone.siddharoopa.data.model.Category
import com.nascriptone.siddharoopa.data.model.Gender
import com.nascriptone.siddharoopa.data.model.Sound
import com.nascriptone.siddharoopa.data.model.entity.Sabda

sealed interface FilterState {
    data class Success(val data: List<Sabda>) : FilterState
    data object Loading : FilterState
    data class Error(val message: String) : FilterState
}

data class Filter(
    val category: Category? = null,
    val sound: Sound? = null,
    val gender: Gender? = null,
)



