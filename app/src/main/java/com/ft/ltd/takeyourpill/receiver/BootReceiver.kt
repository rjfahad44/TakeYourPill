package com.ft.ltd.takeyourpill.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import com.ft.ltd.takeyourpill.reminder.ReminderManager
import com.ft.ltd.takeyourpill.repository.PillRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var pillRepository: PillRepository

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.IO).launch {
            pillRepository.getAllPills().forEach {
                ReminderManager.planNextPillReminder(context, it)
            }
        }
    }
}