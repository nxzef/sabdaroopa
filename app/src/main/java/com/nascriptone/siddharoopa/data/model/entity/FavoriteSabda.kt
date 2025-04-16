package com.nascriptone.siddharoopa.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_sabda")
data class FavoriteSabda(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val favSabdaId: Int,
    val favSabdaCategory: String
)