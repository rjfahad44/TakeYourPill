package com.ft.ltd.takeyourpill.adapter.viewholder

import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.ft.ltd.takeyourpill.databinding.ItemHistoryBinding
import com.ft.ltd.takeyourpill.model.History
import com.ft.ltd.takeyourpill.utils.onClick
import com.ft.ltd.takeyourpill.utils.setDateText
import com.ft.ltd.takeyourpill.utils.setTimeText
import com.ft.ltd.takeyourpill.utils.setVerticalBias

class HistoryItemViewHolder(
    private val binding: ItemHistoryBinding,
    private val optionsClickListener: (View, History, Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(
        history: History, isFirstInList: Boolean, isFirstOfDate: Boolean, position: Int,
        showNames: Boolean
    ) = binding.run {

        textHistoryAmount.text = history.amount
        buttonShowMore.onClick {
            optionsClickListener(it, history, position)
        }

        textHistoryReminded.setTimeText(history.reminded.time)

        textHistoryConfirmed.isInvisible = !history.hasBeenConfirmed
        textHistoryConfirmed.setTimeText(history.confirmed?.time ?: history.reminded.time)

        textDate.setDateText(history.reminded.time)

        textPillName.text = history.pillName
        textPillName.isVisible = showNames

        // Should this item show a divider
        divider.isVisible = isFirstOfDate && !isFirstInList
        dividerPill.isVisible = !isFirstOfDate

        if (showNames) {
            textHistoryConfirmed.setVerticalBias(1.0f)
            textDate.isVisible = isFirstOfDate
        } else {
            textHistoryConfirmed.setVerticalBias(0.5f)
            textDate.isInvisible = !isFirstOfDate
        }
    }
}