package com.nascriptone.siddharoopa.data.local.converter

import androidx.room.TypeConverter
import com.nascriptone.siddharoopa.data.model.Category
import com.nascriptone.siddharoopa.data.model.Declension
import com.nascriptone.siddharoopa.data.model.Gender
import com.nascriptone.siddharoopa.data.model.Sound
import kotlinx.serialization.json.Json

class Converters {

    companion object {
        private val json = Json { encodeDefaults = true; ignoreUnknownKeys = true }
    }

    @TypeConverter
    fun fromSound(sound: Sound): String = sound.name

    @TypeConverter
    fun toSound(data: String): Sound = Sound.valueOf(data)

    @TypeConverter
    fun fromGender(gender: Gender): String = gender.name

    @TypeConverter
    fun toGender(data: String): Gender = Gender.valueOf(data)

    @TypeConverter
    fun fromCategory(category: Category): String = category.name

    @TypeConverter
    fun toCategory(data: String): Category = Category.valueOf(data)

     // Declension
    @Suppress("unused")
    @TypeConverter
    fun fromDeclension(declension: Declension): String {
        return json.encodeToString(declension)
    }

    @TypeConverter
    fun toDeclension(data: String): Declension {
        return json.decodeFromString(data)
    }
}