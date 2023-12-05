package com.ft.ltd.takeyourpill.fragment

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import com.ft.ltd.takeyourpill.R
import com.ft.ltd.takeyourpill.adapter.AppRecyclerAdapter
import com.ft.ltd.takeyourpill.databinding.FragmentHistoryOverviewBinding
import com.ft.ltd.takeyourpill.utils.navigateSafe
import com.ft.ltd.takeyourpill.utils.tryIgnore
import com.ft.ltd.takeyourpill.model.BaseModel
import com.ft.ltd.takeyourpill.model.HistoryOverallItem
import com.ft.ltd.takeyourpill.model.Pill
import com.ft.ltd.takeyourpill.viewmodel.MainViewModel
import com.ft.ltd.takeyourpill.viewmodel.history.HistoryOverviewViewModel

@AndroidEntryPoint
class HistoryOverviewFragment : Fragment(R.layout.fragment_history_overview) {

    private val model: HistoryOverviewViewModel by viewModels()
    private val mainModel: MainViewModel by activityViewModels()

    private val binding by viewBinding(FragmentHistoryOverviewBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appAdapter = AppRecyclerAdapter(
            requireActivity(),
            null,
            getString(R.string.no_history),
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_hourglass_empty),
            mainModel.adMobAdsManager
        )

        appAdapter.setOnItemClickListener { _, item -> onHistoryClicked(item) }

        binding.recyclerHistory.adapter = appAdapter

        model.historyStats.observe(viewLifecycleOwner) { result ->
            result
                .onSuccess { appAdapter.submitList(it) }
                .onFailure {
                    tryIgnore { (requireParentFragment() as HistoryFragment).disableTabs() }
                    appAdapter.submitList(listOf()) // Submit an empty list to show the adapter
                }
        }

        mainModel.shouldScrollUp.observe(viewLifecycleOwner) {
            if (isVisible) binding.recyclerHistory.smoothScrollToPosition(0)
        }

    }

    private fun onHistoryClicked(item: BaseModel) {
        if (item is Pill) {
            val directions = HistoryFragmentDirections.actionHistoryToFragmentHistoryView(item.id)
            findNavController().navigateSafe(directions, R.id.history)
        } else if (item is HistoryOverallItem) {
            val directions = HistoryFragmentDirections.actionHistoryToFragmentHistoryView(-1, true)
            findNavController().navigateSafe(directions, R.id.history)
        }
    }
}