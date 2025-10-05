package com.nascriptone.siddharoopa.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

@Fts4(contentEntity = Sabda::class)
@Entity(tableName = "sabda_fts")
data class SabdaFts(
    val word: String,
    val translit: String,
    @ColumnInfo(name = "translit_normalized")
    val translitNormalized: String,
    val meaning: String
)