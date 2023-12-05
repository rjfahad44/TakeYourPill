package com.ft.ltd.takeyourpill.viewmodel

import android.content.Context
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import com.ft.ltd.takeyourpill.utils.hour
import com.ft.ltd.takeyourpill.utils.minute
import com.ft.ltd.takeyourpill.model.History
import com.ft.ltd.takeyourpill.reminder.ReminderUtil
import com.ft.ltd.takeyourpill.repository.PillRepository
import com.ft.ltd.takeyourpill.repository.ReminderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    pillRepository: PillRepository,
    private val reminderRepository: ReminderRepository
) : ViewModel() {
    private val refreshTrigger = MutableLiveData(Unit)
    val allPills = refreshTrigger.switchMap {
        pillRepository.getAllPillsWithHistoryFlow().asLiveData()
    }

    fun refreshPills() {
        refreshTrigger.value = Unit
    }

    fun confirmPill(applicationContext: Context, history: History) =
        liveData(Dispatchers.Default) {
            val reminderTime = Calendar.getInstance().apply {
                clear()
                hour = history.reminded.hour
                minute = history.reminded.minute
            }.timeInMillis

            reminderRepository.getRemindersBasedOnTime(reminderTime)
                .firstOrNull { it.pillId == history.pillId }
                ?.let { reminder ->
                    val confirmIntent = ReminderUtil.getConfirmIntent(
                        applicationContext,
                        reminder.id,
                        history.pillId,
                        history.reminded.timeInMillis
                    )
                    applicationContext.sendBroadcast(confirmIntent)
                    delay(200) // Wait for broadcast to finish
                    emit(true)
                } ?: emit(false)
        }
}