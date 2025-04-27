package com.nascriptone.siddharoopa.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteTable
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nascriptone.siddharoopa.data.local.converter.Converter
import com.nascriptone.siddharoopa.data.local.dao.GeneralSabdaDao
import com.nascriptone.siddharoopa.data.local.dao.RestPropDao
import com.nascriptone.siddharoopa.data.local.dao.SpecificSabdaDao
import com.nascriptone.siddharoopa.data.model.entity.GeneralSabda
import com.nascriptone.siddharoopa.data.model.entity.RestProp
import com.nascriptone.siddharoopa.data.model.entity.SpecificSabda


@Database(
    entities = [GeneralSabda::class, SpecificSabda::class, RestProp::class],
    version = AppDatabase.LATEST_VERSION,
    autoMigrations = [
        AutoMigration(1, 2),
        AutoMigration(2, 3, AppDatabase.AutoMigration3::class)
    ],
    exportSchema = true
)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        const val LATEST_VERSION = 3
    }

    abstract fun generalSabdaDao(): GeneralSabdaDao
    abstract fun specificSabdaDao(): SpecificSabdaDao
    abstract fun restPropDao(): RestPropDao


    @DeleteTable.Entries(
        DeleteTable(tableName = "favorite_sabda")
    )
    class AutoMigration3 : AutoMigrationSpec {
        override fun onPostMigrate(db: SupportSQLiteDatabase) {
            super.onPostMigrate(db)
        }
    }


}