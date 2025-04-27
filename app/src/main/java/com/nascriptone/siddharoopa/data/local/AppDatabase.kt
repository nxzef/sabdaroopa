package com.nascriptone.siddharoopa.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.DeleteTable
import androidx.room.RenameColumn
import androidx.room.RenameTable
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
        AutoMigration(2, 3, AppDatabase.AutoMigration3::class),
        AutoMigration(3, 4, AppDatabase.AutoMigration4::class)
    ],
    exportSchema = true
)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        const val LATEST_VERSION = 4
    }

    abstract fun generalSabdaDao(): GeneralSabdaDao
    abstract fun specificSabdaDao(): SpecificSabdaDao
    abstract fun restPropDao(): RestPropDao


    @RenameTable("favorite_sabda", "rest_prop")
    @RenameColumn("favorite_sabda", "favSabdaId", "favorite")
    @DeleteColumn("favorite_sabda", "favSabdaCategory")
    class AutoMigration3 : AutoMigrationSpec {
        override fun onPostMigrate(db: SupportSQLiteDatabase) {
            super.onPostMigrate(db)
        }
    }


    @DeleteTable.Entries(
        DeleteTable(tableName = "rest_prop")
    )
    class AutoMigration4 : AutoMigrationSpec {
        override fun onPostMigrate(db: SupportSQLiteDatabase) {
            super.onPostMigrate(db)
        }
    }


}