package com.nascriptone.siddharoopa.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nascriptone.siddharoopa.data.model.uiobj.Table

@Entity(tableName = "rest_props")
data class RestProp(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val favorite: Favorite? = null
)

data class Favorite(
    val id: Int,
    val table: Table,
    val timestamp: Long = System.currentTimeMillis()
)