package com.nascriptone.siddharoopa.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.nascriptone.siddharoopa.data.model.entity.Favorite

class Converter {
    companion object {
        private val gson = Gson()
    }

    // Favorite

    @TypeConverter
    fun fromFavorite(favorite: Favorite): String {
        return gson.toJson(favorite)
    }

    @TypeConverter
    fun toFavorite(data: String): Favorite {
        return gson.fromJson(data, Favorite::class.java)
    }
}