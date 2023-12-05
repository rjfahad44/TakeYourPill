package com.ft.ltd.takeyourpill.adapter.viewholder

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.ft.ltd.takeyourpill.R
import com.ft.ltd.takeyourpill.databinding.ItemHistoryPillBinding
import com.ft.ltd.takeyourpill.utils.context
import com.ft.ltd.takeyourpill.utils.onClick
import com.ft.ltd.takeyourpill.utils.setBackgroundColorShaped
import com.ft.ltd.takeyourpill.model.BaseModel
import com.ft.ltd.takeyourpill.model.HistoryOverallItem
import com.ft.ltd.takeyourpill.model.HistoryPillItem
import com.ft.ltd.takeyourpill.model.Pill

class HistoryViewHolder(
    private val binding: ItemHistoryPillBinding,
    private val clickListener: (View, BaseModel) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(historyPill: HistoryPillItem) = binding.run {
        when (historyPill.historyType) {
            is HistoryOverallItem -> {
                viewPillColor.isVisible = false
                textHistoryName.text = binding.root.context.getString(R.string.stat_overall)
            }
            is Pill -> {
                viewPillColor.isVisible = true
                viewPillColor.setBackgroundColorShaped(historyPill.historyType.colorResource(context))
                textHistoryName.text = historyPill.historyType.name
            }
        }

        cardHistoryPill.onClick { view ->
            clickListener(view, historyPill.historyType)
        }

        textHistoryDescription.text = historyPill.stat.getSummaryText(context)
        textHistoryDescription.isVisible = historyPill.stat.hasStats
    }
}
