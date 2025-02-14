package com.nascriptone.siddharoopa.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nascriptone.siddharoopa.data.local.AppDatabase
import com.nascriptone.siddharoopa.data.local.SabdaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database.db"
        )
            .createFromAsset("app_database.db")
            .fallbackToDestructiveMigration()
            .build()
    }



    @Provides
    fun provideSabdaDao(db: AppDatabase): SabdaDao {
        return db.SabdaDao()
    }

}