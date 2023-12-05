package com.ft.ltd.takeyourpill.adapter.viewholder

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.ft.ltd.takeyourpill.databinding.ItemReminderBinding
import com.ft.ltd.takeyourpill.utils.context
import com.ft.ltd.takeyourpill.utils.onClick
import com.ft.ltd.takeyourpill.model.Reminder

class ReminderViewHolder(
    private val binding: ItemReminderBinding,
    private val clickListener: (View, Reminder) -> Unit,
    private val deleteListener: (View, Reminder) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(reminder: Reminder, showDelete: Boolean, showRipple: Boolean) = binding.run {
        textReminderTime.text = reminder.formattedString(context)

        buttonDeleteReminder.isVisible = showDelete
        if (!showRipple) reminderContainer.background = null

        reminderContainer.onClick { view -> clickListener(view, reminder) }
        buttonDeleteReminder.onClick { view -> deleteListener(view, reminder) }
    }
}
