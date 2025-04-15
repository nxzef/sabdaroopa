package com.nascriptone.siddharoopa.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.nascriptone.siddharoopa.data.model.entity.FavoriteSabda

@Dao
interface FavoriteSabdaDao {
    @Query("SELECT * FROM favorite_sabda")
    suspend fun getAllSabda(): List<FavoriteSabda>
}