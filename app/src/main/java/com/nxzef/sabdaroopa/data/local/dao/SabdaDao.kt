package com.nxzef.sabdaroopa.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.nxzef.sabdaroopa.data.model.Category
import com.nxzef.sabdaroopa.data.model.Gender
import com.nxzef.sabdaroopa.data.model.Sound
import com.nxzef.sabdaroopa.data.model.entity.Sabda
import kotlinx.coroutines.flow.Flow

@Dao
interface SabdaDao {

    @Query("SELECT * FROM sabda")
    suspend fun getEntireList(): List<Sabda>

    @Query(
        """
    SELECT *
    FROM (
        SELECT * FROM sabda
        WHERE 
            (
                (:query IS NULL OR :query = '')
                OR (sabda.rowid IN (
                    SELECT sabda_fts.rowid
                    FROM sabda_fts
                    WHERE sabda_fts MATCH :query
                ))
            )
            AND (:category IS NULL OR sabda.category = :category)
            AND (:sound IS NULL OR sabda.sound = :sound)
            AND (:gender IS NULL OR sabda.gender = :gender)
    ) AS combined
    ORDER BY
        CASE WHEN visit_count > 0 THEN 0 ELSE 1 END,
        visit_count DESC,
        last_visited DESC,
        CASE
            WHEN :query IS NULL OR :query = '' THEN 0
            WHEN word LIKE :exactMatch THEN 1
            WHEN meaning LIKE :exactMatch THEN 2
            WHEN translit_normalized LIKE :exactMatch THEN 3
            WHEN translit LIKE :exactMatch THEN 4
            ELSE 5
        END,
        word ASC
"""
    )
    fun getSabdaWithFilters(
        query: String?,
        exactMatch: String?,
        category: Category?,
        sound: Sound?,
        gender: Gender?
    ): PagingSource<Int, Sabda>

    @Query(
        """
        UPDATE sabda 
        SET visit_count = visit_count + 1,
            last_visited = :timestamp
        WHERE id = :sabdaId
    """
    )
    suspend fun incrementVisitCount(sabdaId: Int, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT * FROM sabda WHERE is_favorite = 1 ORDER BY favorite_since DESC")
    fun getFavoriteList(): PagingSource<Int, Sabda>

    @Query("SELECT id FROM sabda WHERE is_favorite = 1")
    fun getFavoriteIds(): Flow<List<Int>>

    @Query("UPDATE sabda SET is_favorite = CASE WHEN is_favorite = 1 THEN 0 ELSE 1 END, favorite_since = CASE WHEN is_favorite = 1 THEN NULL ELSE :timeStamp END WHERE id = :id")
    suspend fun toggleFavoriteInternal(id: Int, timeStamp: Long?)

    @Query("SELECT is_favorite FROM sabda WHERE id = :id")
    suspend fun getFavoriteState(id: Int): Int

    @Query("UPDATE sabda SET is_favorite = 0, favorite_since = NULL WHERE id IN (:ids)")
    suspend fun removeItemsFromFavorite(ids: Set<Int>): Int

    @Query("UPDATE sabda SET is_favorite = 1, favorite_since = :timeStamp WHERE id IN (:ids) AND is_favorite = 0")
    suspend fun addItemsToFavorite(ids: Set<Int>, timeStamp: Long): Int

    @Query("SELECT * FROM sabda WHERE id = :id")
    fun findSabdaById(id: Int): Flow<Sabda?>

    @Query("SELECT word FROM sabda WHERE id IN (:ids)")
    suspend fun getWords(ids: Set<Int>): List<String>

    @Query("SELECT * FROM sabda WHERE (:category IS NULL OR category = :category) AND (:sound IS NULL OR sound = :sound) AND (:gender IS NULL OR gender = :gender)")
    suspend fun getSabdaListByFilter(
        category: Category?, sound: Sound?, gender: Gender?
    ): List<Sabda>

    @Query("SELECT * FROM sabda WHERE id IN (:ids)")
    suspend fun getSabdaListByIdSet(ids: Set<Int>): List<Sabda>

    @Query("SELECT EXISTS(SELECT 1 FROM sabda WHERE id IN (:ids) AND is_favorite = 0)")
    fun hasAnyNonFavorite(ids: Set<Int>): Flow<Boolean>

}