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


/**
 * Dagger Hilt module that provides dependencies for the application.
 */
@InstallIn(SingletonComponent::class) // Installs AppModule in the generated SingletonComponent.
@Module
object AppModule {

    /**
     * Provides a singleton instance of the DiaryEntriesDatabaseDao interface for accessing Diary Entry data in the database.
     *
     * @param diaryDatabase The DiaryDatabase instance injected by Dagger Hilt.
     * @return The DiaryEntriesDatabaseDao instance.
     */
    @Singleton
    @Provides
    fun provideDiaryEntriesDao(diaryDatabase: DiaryDatabase): DiaryEntriesDatabaseDao =
        diaryDatabase.diaryEntriesDao()

    /**
     * Provides a singleton instance of the PhotoDatabaseDao interface for accessing Photo data in the database.
     *
     * @param diaryDatabase The DiaryDatabase instance injected by Dagger Hilt.
     * @return The PhotoDatabaseDao instance.
     */
    @Singleton
    @Provides
    fun providePhotosDao(diaryDatabase: DiaryDatabase): PhotoDatabaseDao = diaryDatabase.photosDao()

    /**
     * Provides a singleton instance of the DiaryDatabase, which is the Room database for the application.
     *
     * @param context The application context injected by Dagger Hilt.
     * @return The DiaryDatabase instance.
     */
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): DiaryDatabase =
        Room.databaseBuilder(
            context, DiaryDatabase::class.java, "diary_db"
        ).fallbackToDestructiveMigration().build()
}
