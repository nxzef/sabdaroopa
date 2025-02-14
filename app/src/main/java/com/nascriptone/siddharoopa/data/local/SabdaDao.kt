package com.nascriptone.siddharoopa.data.local

import androidx.room.Dao
import androidx.room.Query
import com.nascriptone.siddharoopa.data.model.Sabda

@Dao
interface SabdaDao {
    @Query("SELECT * FROM sabda")
    suspend fun getAllSabda(): List<Sabda>
}