package com.nascriptone.siddharoopa.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nascriptone.siddharoopa.data.model.Category
import com.nascriptone.siddharoopa.data.model.Declension
import com.nascriptone.siddharoopa.data.model.Gender
import com.nascriptone.siddharoopa.data.model.Sound

@Entity(
    tableName = "sabda",
    indices = [
        Index(value = ["category"], name = "idx_sabda_category"),
        Index(value = ["gender"], name = "idx_sabda_gender"),
        Index(value = ["sound"], name = "idx_sabda_sound")
    ]
)
data class Sabda(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val anta: String,
    val word: String,
    val sound: Sound,
    val gender: Gender,
    val meaning: String,
    val category: Category,
    val translit: String,
    @ColumnInfo(name = "translit_normalized", defaultValue = "")
    val translitNormalized: String,
    @ColumnInfo(name = "is_favorite", defaultValue = "0")
    val isFavorite: Boolean,
    @ColumnInfo(name = "favorite_since")
    val favoriteSince: Long?,
    @ColumnInfo(name = "visit_count", defaultValue = "0")
    val visitCount: Int,
    @ColumnInfo(name = "last_visited")
    val lastVisited: Long?,
    val declension: Declension
)