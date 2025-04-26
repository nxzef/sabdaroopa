package com.nascriptone.siddharoopa.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.nascriptone.siddharoopa.data.local.AppDatabase
import com.nascriptone.siddharoopa.data.local.dao.RestPropDao
import com.nascriptone.siddharoopa.data.local.dao.GeneralSabdaDao
import com.nascriptone.siddharoopa.data.local.dao.SpecificSabdaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


private const val USER_PREFERENCE_NAME = "user_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(USER_PREFERENCE_NAME)

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
            .build()
    }

    @Provides
    fun provideGeneralSabdaDao(db: AppDatabase): GeneralSabdaDao {
        return db.generalSabdaDao()
    }

    @Provides
    fun provideSpecificSabdaDao(db: AppDatabase): SpecificSabdaDao {
        return db.specificSabdaDao()
    }

    @Provides
    fun provideRestPropDao(db: AppDatabase): RestPropDao {
        return db.restPropDao()
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

}