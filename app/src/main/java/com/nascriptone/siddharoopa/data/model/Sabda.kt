package com.nascriptone.siddharoopa.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sabda")
data class Sabda(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val word: String?,
    val phonetic: String?,
    val gender: String?,
    val category: String?,
    val anka: String?,
    val pos: String?,
    val declension: String?
)