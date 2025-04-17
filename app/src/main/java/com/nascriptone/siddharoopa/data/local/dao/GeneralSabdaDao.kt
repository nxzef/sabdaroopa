package com.nascriptone.siddharoopa.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.nascriptone.siddharoopa.data.model.entity.GeneralSabda

@Dao
interface GeneralSabdaDao {
    @Query("SELECT * FROM general_sabda")
    suspend fun getAllSabda(): List<GeneralSabda>

    @Query("SELECT * FROM general_sabda WHERE id IN (:ids)")
    suspend fun getFavoritesSabda(ids: List<Int>): List<GeneralSabda>
}