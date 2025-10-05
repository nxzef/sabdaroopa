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

    @Query("SELECT * FROM sabda")
    suspend fun getEntireList(): List<Sabda>

    @Query("""
    SELECT sabda.* 
    FROM sabda
    JOIN sabda_fts ON sabda.rowid = sabda_fts.rowid
    WHERE sabda_fts MATCH :query
    ORDER BY 
        CASE 
            WHEN sabda.visit_count > 0 THEN 0 
            ELSE 1 
        END,
        sabda.visit_count DESC,
        sabda.last_visited DESC,
        CASE 
            WHEN sabda.word LIKE :exactMatch THEN 1
            WHEN sabda.translit LIKE :exactMatch THEN 2
            WHEN sabda.translit_normalized LIKE :exactMatch THEN 3
            WHEN sabda.meaning LIKE :exactMatch THEN 4
            ELSE 5
        END,
        sabda.word ASC
    LIMIT :limit
""")
    fun searchSabda(
        query: String,
        exactMatch: String,
        limit: Int = 50
    ): Flow<List<Sabda>>

    @Query("""
        SELECT * FROM sabda 
        WHERE visit_count > 0 
        ORDER BY last_visited DESC, visit_count DESC
        LIMIT :limit
    """)
    fun getRecentlyVisited(limit: Int): Flow<List<Sabda>>

    @Query("""
        UPDATE sabda 
        SET visit_count = visit_count + 1,
            last_visited = :timestamp
        WHERE id = :sabdaId
    """)
    suspend fun incrementVisitCount(sabdaId: Int, timestamp: Long = System.currentTimeMillis())

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
        category: Category?, sound: Sound?, gender: Gender?
    ): PagingSource<Int, Sabda>

    @Query("SELECT word FROM sabda WHERE id IN (:ids)")
    suspend fun getWords(ids: Set<Int>): List<String>

    @Query("SELECT * FROM sabda WHERE (:category IS NULL OR category = :category) AND (:sound IS NULL OR sound = :sound) AND (:gender IS NULL OR gender = :gender)")
    suspend fun getSabdaListByFilter(
        category: Category?, sound: Sound?, gender: Gender?
    ): List<Sabda>

    @Query("SELECT * FROM sabda WHERE id IN (:ids)")
    suspend fun getSabdaListByIdSet(ids: Set<Int>): List<Sabda>

}