package com.ft.ltd.takeyourpill.adapter.viewholder

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.ft.ltd.takeyourpill.R
import com.ft.ltd.takeyourpill.databinding.ItemPillBinding
import com.ft.ltd.takeyourpill.utils.*
import com.ft.ltd.takeyourpill.model.BaseModel
import com.ft.ltd.takeyourpill.model.History
import com.ft.ltd.takeyourpill.model.Pill
import com.ft.ltd.takeyourpill.model.Reminder
import java.util.*

class PillViewHolder(
    private val binding: ItemPillBinding,
    private val onPillClick: (View, BaseModel) -> Unit,
    private val onConfirmClick: (History) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(pill: Pill) = binding.run {

        cardPill.apply {
            transitionName = "${pill.id}"
            onClick { onPillClick(it, pill) }
        }

        imagePillColor.setBackgroundColorShaped(pill.colorResource(context))

        textPillName.text = pill.name

        imagePillPhoto.apply {
            setImageDrawable(pill.getPhotoDrawable(context))
            isVisible = pill.isPhotoVisible
        }

        pillDescription.apply {
            text = getFormattedDescription(pill)
            isVisible = text.isNotBlank()
        }

        setupReminders(pill.reminders)
        setupIntake(pill, context)
        setupConfirm(pill, context)
    }

    private fun setupReminders(reminders: List<Reminder>) = binding.chipsLayout.run {
        removeAllViews()
        reminders.sortedBy { rem -> rem.time.timeInMillis }.forEach { reminder ->
            val chip = getChip(reminder.getAmountTimeString(binding.context))
            addView(chip)
        }
    }

    private fun getChip(chipText: String) = Chip(binding.context).apply {
        text = chipText
        isFocusable = false
        isClickable = false
        stateListAnimator = null
        rippleColor = null
        chipBackgroundColor = null
        setChipStrokeColorResource(R.color.stroke_color)
        setChipStrokeWidthResource(R.dimen.stroke_width)
    }

    private fun setupIntake(pill: Pill, context: Context) {
        // Don't modify original pill options
        val options = pill.options.copy()
        // If last reminder date is null, then this is the first reminder
        pill.lastReminderDate?.let { lastDate ->
            // Only add next cycle if this is the first reminder today
            if (lastDate.DayOfYear != Calendar.getInstance().DayOfYear) {
                options.nextCycleIteration()
            }
        }

        with(options) {
            when {
                isIndefinite() -> {
                    showIntakeOptions(false)
                }
                isFinite() -> {
                    showIntakeOptions(true)
                    binding.textIntakeTitle.text =
                        context.getString(R.string.intake_options_x_days)
                    if (isActive()) {
                        binding.textIntakeOptions.text = context.getString(
                            R.string.intake_options_x_days_text,
                            daysActive,
                            daysActive - todayCycle + 1
                        )
                    } else {
                        binding.textIntakeOptions.text = context.getString(R.string.inactive)
                    }
                }
                isCycle() -> {
                    showIntakeOptions(true)
                    binding.textIntakeTitle.text = context.getString(R.string.cycle)
                    binding.textIntakeOptions.text =
                        context.getString(
                            R.string.intake_options_cycle,
                            daysActive,
                            daysInactive,
                            todayCycle
                        )
                }
            }
        }
    }

    private fun showIntakeOptions(visible: Boolean) = binding.run {
        textIntakeTitle.isVisible = visible
        textIntakeOptions.isVisible = visible
        divider.isVisible = visible
    }

    private fun setupConfirm(pill: Pill?, context: Context) = binding.run {
        pillConfirm.isVisible = false
        pill?.closeHistory?.let { history ->
            pillConfirm.isVisible = true
            buttonTaken.backgroundTintList = ColorStateList.valueOf(pill.colorResource(context))
            textQuestionTake.text = binding.root.context.getString(
                R.string.pill_taken_question,
                history.amount,
                history.reminded.time.getTimeString(context)
            )
            buttonTaken.onClick { onConfirmClick(history) }
        }
    }

    private fun getFormattedDescription(pill: Pill) =
        pill.description?.let { desc ->
            var oneLineDesc = desc.split("\n")[0]
            if (desc.contains("\n")) {
                oneLineDesc += "…"
            }
            oneLineDesc
        } ?: ""
}