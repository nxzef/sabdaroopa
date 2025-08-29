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
import com.nascriptone.siddharoopa.data.local.converter.Converters
import com.nascriptone.siddharoopa.data.local.dao.SabdaDao
import com.nascriptone.siddharoopa.data.model.entity.Sabda


@Database(
    entities = [Sabda::class],
    version = AppDatabase.LATEST_VERSION,
    autoMigrations = [
        AutoMigration(1, 2),
        AutoMigration(2, 3, AppDatabase.AutoMigration3::class),
        AutoMigration(3, 4, AppDatabase.AutoMigration4::class),
        AutoMigration(4, 5, AppDatabase.AutoMigration5::class),
    ],
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        const val LATEST_VERSION = 5
    }

    abstract fun sabdaDao(): SabdaDao

    @RenameTable("favorite_sabda", "rest_prop")
    @RenameColumn("favorite_sabda", "favSabdaId", "favorite")
    @DeleteColumn("favorite_sabda", "favSabdaCategory")
    class AutoMigration3 : AutoMigrationSpec

    @DeleteTable.Entries(
        DeleteTable(tableName = "rest_prop")
    )
    class AutoMigration4 : AutoMigrationSpec

    @DeleteTable("rest_props")
    @DeleteTable("specific_sabda")
    @DeleteTable("general_sabda")
    class AutoMigration5 : AutoMigrationSpec

}