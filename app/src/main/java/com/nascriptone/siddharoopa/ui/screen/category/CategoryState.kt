package com.nascriptone.siddharoopa.ui.screen.category

import com.nascriptone.siddharoopa.data.model.Category
import com.nascriptone.siddharoopa.data.model.Gender
import com.nascriptone.siddharoopa.data.model.Sound

data class Filter(
    val category: Category? = null,
    val sound: Sound? = null,
    val gender: Gender? = null,
)



