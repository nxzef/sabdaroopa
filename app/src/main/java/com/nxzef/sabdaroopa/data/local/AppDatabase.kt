package com.nxzef.sabdaroopa.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nxzef.sabdaroopa.data.local.converter.Converters
import com.nxzef.sabdaroopa.data.local.dao.SabdaDao
import com.nxzef.sabdaroopa.data.model.entity.Sabda
import com.nxzef.sabdaroopa.data.model.entity.SabdaFts


@Database(
    entities = [Sabda::class, SabdaFts::class],
    version = AppDatabase.LATEST_VERSION
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        const val LATEST_VERSION = 1
    }

    abstract fun sabdaDao(): SabdaDao
}