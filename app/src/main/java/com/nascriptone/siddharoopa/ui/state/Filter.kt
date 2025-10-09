package com.nascriptone.siddharoopa.ui.state

import com.nascriptone.siddharoopa.data.model.Category
import com.nascriptone.siddharoopa.data.model.Gender
import com.nascriptone.siddharoopa.data.model.Sound

data class Filter(
    val category: Category? = null,
    val sound: Sound? = null,
    val gender: Gender? = null,
) {
    val isActive: Boolean
        get() = category != null || sound != null || gender != null

    val activeFilterCount: Int
        get() = listOfNotNull(category, sound, gender).size
}