package com.ft.ltd.takeyourpill.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ft.ltd.takeyourpill.model.History
import com.ft.ltd.takeyourpill.model.PillEntity
import com.ft.ltd.takeyourpill.model.Reminder

@Database(
    entities = [PillEntity::class, Reminder::class, History::class],
    version = 6
)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun getPillDao(): PillDao
    abstract fun getReminderDao(): ReminderDao
    abstract fun getHistoryDao(): HistoryDao
}