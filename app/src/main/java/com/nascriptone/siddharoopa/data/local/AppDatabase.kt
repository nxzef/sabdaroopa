package com.nascriptone.siddharoopa.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.nascriptone.siddharoopa.data.local.dao.FavoriteSabdaDao
import com.nascriptone.siddharoopa.data.local.dao.GeneralSabdaDao
import com.nascriptone.siddharoopa.data.local.dao.SpecificSabdaDao
import com.nascriptone.siddharoopa.data.model.entity.FavoriteSabda
import com.nascriptone.siddharoopa.data.model.entity.GeneralSabda
import com.nascriptone.siddharoopa.data.model.entity.SpecificSabda


@Database(
    entities = [GeneralSabda::class, SpecificSabda::class, FavoriteSabda::class],
    version = AppDatabase.LATEST_VERSION,
    autoMigrations = [
        AutoMigration(1, 2)
    ],
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        const val LATEST_VERSION = 2
    }

    abstract fun generalSabdaDao(): GeneralSabdaDao
    abstract fun specificSabdaDao(): SpecificSabdaDao
    abstract fun favoriteSabdaDao(): FavoriteSabdaDao
}