package com.nascriptone.siddharoopa.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nascriptone.siddharoopa.data.model.entity.FavoriteSabda

@Dao
interface FavoriteSabdaDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavoriteSabda(favoriteSabda: FavoriteSabda)

    @Query("SELECT * FROM favorite_sabda")
    suspend fun getAllSabda(): List<FavoriteSabda>

    @Query("DELETE FROM favorite_sabda WHERE favSabdaId = :id AND favSabdaCategory = :category")
    suspend fun removeFavoriteSabda(id: Int, category: String)

    @Query(
        "SELECT EXISTS(SELECT 1 FROM favorite_sabda WHERE favSabdaId = :id AND favSabdaCategory = :category)"
    )
    suspend fun isFavoriteExists(id: Int, category: String): Boolean

}