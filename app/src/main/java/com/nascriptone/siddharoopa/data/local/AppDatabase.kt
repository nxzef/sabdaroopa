package com.nascriptone.siddharoopa.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RenameColumn
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import com.nascriptone.siddharoopa.data.local.converter.Converters
import com.nascriptone.siddharoopa.data.local.dao.SabdaDao
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.data.model.entity.SabdaFts


@Database(
    entities = [Sabda::class, SabdaFts::class],
    version = AppDatabase.LATEST_VERSION,
    autoMigrations = [
        AutoMigration(1, 2, spec = AppDatabase.AutoMigration2::class)
    ]
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        const val LATEST_VERSION = 2
    }

    abstract fun sabdaDao(): SabdaDao

    @RenameColumn(tableName = "sabda", fromColumnName = "last_visit", toColumnName = "last_visited")
    class AutoMigration2 : AutoMigrationSpec
}