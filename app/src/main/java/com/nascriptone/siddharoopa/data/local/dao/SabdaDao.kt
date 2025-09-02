package com.nascriptone.siddharoopa.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.nascriptone.siddharoopa.data.model.Category
import com.nascriptone.siddharoopa.data.model.Gender
import com.nascriptone.siddharoopa.data.model.Sound
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import kotlinx.coroutines.flow.Flow

@Dao
interface SabdaDao {

    @Query("SELECT * FROM sabda WHERE is_favorite = 1 ORDER BY favorite_since DESC")
    fun getFavoriteList(): PagingSource<Int, Sabda>

    @Query("SELECT id FROM sabda WHERE is_favorite = 1")
    fun getFavoriteIds(): Flow<List<Int>>

    @Query("UPDATE sabda SET is_favorite = CASE WHEN is_favorite = 1 THEN 0 ELSE 1 END, favorite_since = CASE WHEN is_favorite = 1 THEN NULL ELSE :timeStamp END WHERE id = :id")
    suspend fun toggleFavorite(id: Int, timeStamp: Long?)

    @Query("UPDATE sabda SET is_favorite = 0, favorite_since = NULL WHERE id IN (:ids)")
    suspend fun removeItemsFromFavorite(ids: Set<Int>)

    @Query("SELECT * FROM sabda WHERE id = :id")
    fun findSabdaById(id: Int): Flow<Sabda?>

    @Query("SELECT * FROM sabda WHERE (:category IS NULL OR category = :category) AND (:sound IS NULL OR sound = :sound) AND (:gender IS NULL OR gender = :gender)")
    fun getFilteredList(
        category: Category?,
        sound: Sound?,
        gender: Gender?
    ): PagingSource<Int, Sabda>

}