package com.ft.ltd.takeyourpill.adapter.viewholder

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.ft.ltd.takeyourpill.databinding.ItemColorBinding
import com.ft.ltd.takeyourpill.model.PillColor
import com.ft.ltd.takeyourpill.utils.context
import com.ft.ltd.takeyourpill.utils.onClick
import com.ft.ltd.takeyourpill.utils.setBackgroundColorShaped

class ColorViewHolder(
    private val binding: ItemColorBinding,
    private val listener: (View, PillColor) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(pillColor: PillColor) = binding.run {
        viewPillColor.setBackgroundColorShaped(pillColor.getColor(context))
        check.isVisible = pillColor.isChecked
        pillColorFrame.onClick { view -> listener(view, pillColor) }
    }
}
