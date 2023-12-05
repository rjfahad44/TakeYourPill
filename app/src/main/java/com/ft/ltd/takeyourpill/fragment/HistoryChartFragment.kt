package com.ft.ltd.takeyourpill.fragment

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.utils.Utils
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import com.ft.ltd.takeyourpill.R
import com.ft.ltd.takeyourpill.adapter.AppRecyclerAdapter
import com.ft.ltd.takeyourpill.databinding.FragmentHistoryChartBinding
import com.ft.ltd.takeyourpill.utils.applicationContext
import com.ft.ltd.takeyourpill.model.ChartItem
import com.ft.ltd.takeyourpill.viewmodel.MainViewModel
import com.ft.ltd.takeyourpill.viewmodel.history.HistoryChartViewModel

@AndroidEntryPoint
class HistoryChartFragment : Fragment(R.layout.fragment_history_chart) {

    private val model: HistoryChartViewModel by viewModels()
    private val mainModel: MainViewModel by activityViewModels()

    private val binding by viewBinding(FragmentHistoryChartBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appAdapter = AppRecyclerAdapter(
            requireActivity(),
            null,
            getString(R.string.no_history),
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_hourglass_empty),
            mainModel.adMobAdsManager
        )

        binding.recyclerCharts.adapter = appAdapter

        Utils.init(requireContext())

        val titles = mutableListOf(
            getString(R.string.all_pills_chart),
            getString(R.string.missed_pills),
            getString(R.string.confirmed_missed_pills),
        )

        model.getStatsData(applicationContext).observe(viewLifecycleOwner) { data ->
            val titleIterator = titles.iterator()
            data?.let { list ->
                val chartList = list
                    .map { ChartItem(it, titleIterator.next()) }
                    .filter { it.pieData.entryCount != 0 }
                appAdapter.submitList(chartList)
            }
        }

        mainModel.shouldScrollUp.observe(viewLifecycleOwner) {
            if (isVisible) binding.recyclerCharts.smoothScrollToPosition(0)
        }
    }
}