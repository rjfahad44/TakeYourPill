package com.ft.ltd.takeyourpill.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import com.ft.ltd.takeyourpill.model.Pill
import com.ft.ltd.takeyourpill.model.PillColor
import com.ft.ltd.takeyourpill.model.Reminder
import com.ft.ltd.takeyourpill.reminder.NotificationManager
import com.ft.ltd.takeyourpill.reminder.ReminderManager
import com.ft.ltd.takeyourpill.repository.PillRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
        private val pillRepository: PillRepository
) : ViewModel() {

    fun addPill(pill: Pill, applicationContext: Context) = CoroutineScope(Dispatchers.IO).launch {
        val newPill = pillRepository.insertPillReturn(pill)
        setReminding(newPill, applicationContext)
    }

    fun updatePill(pill: Pill, applicationContext: Context) = CoroutineScope(Dispatchers.IO).launch {
        val newPill = pillRepository.updatePillReturn(pill)
        setReminding(newPill, applicationContext)
    }

    private fun setReminding(pill: Pill, applicationContext: Context) {
        NotificationManager.createNotificationChannel(
            applicationContext,
            pill.id.toString(),
            pill.name
        )
        ReminderManager.planNextPillReminder(applicationContext, pill)
    }

    fun getPillById(pillId: Long) = pillRepository.getPillFlow(pillId).asLiveData()

    fun getNewEmptyPill() = Pill.new()

    var hasPillBeenEdited = false
    lateinit var pill: Pill
    val isPillInitialized
        get() = ::pill.isInitialized

    private val _activeColor: MutableLiveData<PillColor> by lazy {
        MutableLiveData<PillColor>()
    }

    val pillColors = _activeColor.map {
        pill.color = it
        val colors = PillColor.getAllPillColorList()
        for (color in colors) {
            color.isChecked = (color.resource == it.resource)
        }
        colors
    }

    private val _reminders: MutableLiveData<List<Reminder>> by lazy {
        MutableLiveData<List<Reminder>>()
    }

    val reminders = _reminders.map {
        pill.reminders = it.sortedBy { rem -> rem.time.time }.toMutableList()
        pill.reminders
    }

    private val _photoBitmap: MutableLiveData<Bitmap?> by lazy {
        MutableLiveData<Bitmap?>()
    }

    val photoBitmap = _photoBitmap.map {
        pill.photo = it
        it
    }

    fun setActivePillColor(pillColor: PillColor) {
        _activeColor.value = pillColor
        hasPillBeenEdited = true
    }

    fun addReminder(reminder: Reminder) {
        val newList = _reminders.value?.toMutableList()
        newList?.add(reminder)
        hasPillBeenEdited = true
        _reminders.value = newList
    }

    fun removeReminder(reminder: Reminder) {
        val newList = _reminders.value?.toMutableList()
        newList?.remove(reminder)
        hasPillBeenEdited = true
        _reminders.value = newList
    }

    fun editReminder(reminder: Reminder) {
        val newList = _reminders.value?.toMutableList()
        if (reminder.id != 0L) {
            newList?.removeAll { r -> r.id == reminder.id }
        } else {
            newList?.remove(reminder)
        }
        newList?.add(reminder)
        hasPillBeenEdited = true
        _reminders.value = newList
    }

    fun setImage(data: Uri, context: Context) = liveData(Dispatchers.IO) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(data)
            _photoBitmap.postValue(BitmapFactory.decodeStream(inputStream))
            hasPillBeenEdited = true
            emit(true)
        } catch (e: FileNotFoundException) {
            emit(false)
        }
    }

    fun deleteImage() {
        hasPillBeenEdited = true
        _photoBitmap.value = null
    }

    fun initFields() {
        _activeColor.value = pill.color
        _reminders.value = pill.reminders
    }

    var firstNameEdit = true
    var firstDescriptionEdit = true

    fun onNameChanged(text: CharSequence?): Boolean {
        text?.let { pill.name = it.trim().toString() }
        if (firstNameEdit) {
            firstNameEdit = false
        } else {
            hasPillBeenEdited = true
        }
        return text.isNullOrBlank()
    }

    fun onDescriptionChanged(text: CharSequence?) {
        text?.let { pill.description = it.trim().toString() }
        if (firstDescriptionEdit) {
            firstDescriptionEdit = false
        } else {
            hasPillBeenEdited = true
        }
    }
}