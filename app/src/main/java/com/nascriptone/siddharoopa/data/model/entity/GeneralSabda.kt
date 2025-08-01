package com.nascriptone.siddharoopa.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "general_sabda")
data class GeneralSabda(
    @PrimaryKey(autoGenerate = true)
    override val id: Int,
    override val anta: String,
    override val gender: String,
    override val word: String,
    override val sound: String,
    override val translit: String,
    override val declension: String
) : Sabda
