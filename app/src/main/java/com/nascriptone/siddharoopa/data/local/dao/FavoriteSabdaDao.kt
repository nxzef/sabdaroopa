package com.nascriptone.siddharoopa.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nascriptone.siddharoopa.data.model.entity.FavoriteSabda
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteSabdaDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavoriteSabda(favoriteSabda: FavoriteSabda)

    @Query("SELECT * FROM favorite_sabda")
    fun getAllSabda(): Flow<List<FavoriteSabda>>

    @Query("DELETE FROM favorite_sabda WHERE favSabdaId = :id AND favSabdaCategory = :table")
    suspend fun removeFavoriteSabda(id: Int, table: String)

    @Query(
        "SELECT EXISTS(SELECT 1 FROM favorite_sabda WHERE favSabdaId = :id AND favSabdaCategory = :table)"
    )
    suspend fun isFavoriteExists(id: Int, table: String): Boolean

}