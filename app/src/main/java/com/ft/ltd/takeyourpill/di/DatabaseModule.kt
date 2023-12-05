package com.ft.ltd.takeyourpill.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.ft.ltd.takeyourpill.database.Database
import com.ft.ltd.takeyourpill.utils.Prefs
import com.ft.ltd.takeyourpill.utils.Constants
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun providePillDatabase(@ApplicationContext app: Context) = Room.databaseBuilder(app, Database::class.java, Constants.PILL_DATABASE_NAME)
        .fallbackToDestructiveMigration() // TODO Remove for release
        .build()

    @Singleton
    @Provides
    fun providePillDao(db: Database) = db.getPillDao()

    @Singleton
    @Provides
    fun provideReminderDao(db: Database) = db.getReminderDao()

    @Singleton
    @Provides
    fun provideHistoryDao(db: Database) = db.getHistoryDao()

    @Provides
    @Singleton
    fun provideAppPrefs(@ApplicationContext appContext: Context) = Prefs(appContext)
}