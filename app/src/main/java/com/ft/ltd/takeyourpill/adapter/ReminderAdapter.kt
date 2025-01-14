package com.ft.ltd.takeyourpill.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.ft.ltd.takeyourpill.adapter.viewholder.ReminderViewHolder
import com.ft.ltd.takeyourpill.databinding.ItemReminderBinding
import com.ft.ltd.takeyourpill.model.Reminder

class ReminderAdapter(
    private val showDelete: Boolean = true,
    private val showRipple: Boolean = true
) : ListAdapter<Reminder, ReminderViewHolder>(Reminder.DiffCallback) {

    private var clickListener: (View, Reminder) -> Unit = { _, _ -> }
    private var deleteListener: (View, Reminder) -> Unit = { _, _ -> }

    fun onReminderClicked(listener: (View, Reminder) -> Unit) {
        clickListener = listener
    }

    fun onReminderDelete(listener: (View, Reminder) -> Unit) {
        deleteListener = listener
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) =
        holder.bind(getItem(position), showDelete, showRipple)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ReminderViewHolder(
            ItemReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            clickListener,
            deleteListener
        )
}