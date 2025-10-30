package com.nxzef.sabdaroopa.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.room.Room
import com.nxzef.sabdaroopa.data.local.AppDatabase
import com.nxzef.sabdaroopa.data.local.UserPreferencesSerializer
import com.nxzef.sabdaroopa.data.local.dao.SabdaDao
import com.nxzef.sabdaroopa.data.model.UserPreferences
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
            .build()
    }

    @Provides
    @Singleton
    fun provideSabdaDao(db: AppDatabase): SabdaDao = db.sabdaDao()

    @Provides
    @Singleton
    fun provideUserPreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<UserPreferences> {
        return DataStoreFactory.create(
            serializer = UserPreferencesSerializer,
            produceFile = {
                context.dataStoreFile("user_preferences.json")
            }
        )
    }

}