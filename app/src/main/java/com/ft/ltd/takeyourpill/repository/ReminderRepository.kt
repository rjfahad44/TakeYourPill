package com.ft.ltd.takeyourpill.repository

import com.ft.ltd.takeyourpill.database.ReminderDao
import javax.inject.Inject

class ReminderRepository @Inject constructor(
    private val reminderDao: ReminderDao
) {
    suspend fun getReminder(reminderId: Long) = reminderDao.getWithId(reminderId)
    suspend fun getRemindersBasedOnTime(time: Long) = reminderDao.getWithTime(time)
}