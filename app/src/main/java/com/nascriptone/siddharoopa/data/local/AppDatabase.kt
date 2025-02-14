package com.nascriptone.siddharoopa.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nascriptone.siddharoopa.data.model.Sabda

@Database(entities = [Sabda::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun SabdaDao(): SabdaDao
}