package com.nascriptone.siddharoopa.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.nascriptone.siddharoopa.data.model.entity.GeneralSabda
import com.nascriptone.siddharoopa.data.model.entity.SpecificSabda

@Dao
interface SpecificSabdaDao {
    @Query("SELECT * FROM specific_sabda")
    suspend fun getAllSabda(): List<SpecificSabda>

    @Query("SELECT * FROM specific_sabda WHERE id IN (:ids)")
    suspend fun getFavoritesSabda(ids: List<Int>): List<SpecificSabda>
}