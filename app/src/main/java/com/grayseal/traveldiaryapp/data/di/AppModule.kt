package com.grayseal.traveldiaryapp.data.di

import android.content.Context
import androidx.room.Room
import com.grayseal.traveldiaryapp.data.database.DiaryDatabase
import com.grayseal.traveldiaryapp.data.database.DiaryEntriesDatabaseDao
import com.grayseal.traveldiaryapp.data.database.PhotoDatabaseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class) //Installs AppModule in the generated SingletonComponent.
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideDiaryEntriesDao(diaryDatabase: DiaryDatabase): DiaryEntriesDatabaseDao =
        diaryDatabase.diaryEntriesDao()

    @Singleton
    @Provides
    fun providePhotosDao(diaryDatabase: DiaryDatabase): PhotoDatabaseDao = diaryDatabase.photosDao()

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): DiaryDatabase =
        Room.databaseBuilder(
            context, DiaryDatabase::class.java, "diary_db"
        ).fallbackToDestructiveMigration().build()
}