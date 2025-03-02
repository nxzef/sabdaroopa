package com.nascriptone.siddharoopa.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.nascriptone.siddharoopa.data.model.entity.SpecificSabda

@Dao
interface SpecificSabdaDao {
    @Query("SELECT * FROM specific_sabda")
    suspend fun getAllSabda(): List<SpecificSabda>
}