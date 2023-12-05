package com.ft.ltd.takeyourpill.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ft.ltd.takeyourpill.fragment.HistoryChartFragment
import com.ft.ltd.takeyourpill.fragment.HistoryOverviewFragment

class HistoryViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int) =
        when (position) {
            0 -> HistoryOverviewFragment()
            1 -> HistoryChartFragment()
            else -> throw IllegalStateException("Unknown ViewPagerAdapter Fragment")
        }

}