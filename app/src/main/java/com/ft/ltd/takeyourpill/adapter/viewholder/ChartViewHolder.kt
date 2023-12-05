package com.ft.ltd.takeyourpill.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.ft.ltd.takeyourpill.R
import com.ft.ltd.takeyourpill.databinding.ItemChartBinding
import com.ft.ltd.takeyourpill.model.ChartItem
import com.ft.ltd.takeyourpill.utils.context
import com.ft.ltd.takeyourpill.utils.getAttrColor
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.formatter.PercentFormatter

class ChartViewHolder(
    private val binding: ItemChartBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(chartItem: ChartItem) = binding.run {
        chartTitle.text = chartItem.title

        pieChart.apply {
            description.isEnabled = false
            setUsePercentValues(true)
            dragDecelerationFrictionCoef = 0.8f
            animateY(1400, Easing.EaseInOutQuad)
            holeRadius = 25f
            isDrawHoleEnabled = false
            transparentCircleRadius = 30f
            setDrawEntryLabels(true)
        }
        pieChart.legend.apply {
            textColor = context.getAttrColor(com.google.android.material.R.attr.colorOnSurface)
            isWordWrapEnabled = true
            textSize = 12f
            isEnabled = false
        }

        chartItem.pieData.setValueFormatter(PercentFormatter(pieChart))
        pieChart.data = chartItem.pieData
        pieChart.invalidate()
    }
}
