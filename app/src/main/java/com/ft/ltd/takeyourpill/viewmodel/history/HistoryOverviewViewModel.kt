package com.ft.ltd.takeyourpill.viewmodel.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ft.ltd.takeyourpill.model.BaseModel
import com.ft.ltd.takeyourpill.model.HistoryOverallItem
import com.ft.ltd.takeyourpill.model.HistoryPillItem
import com.ft.ltd.takeyourpill.model.StatItem
import dagger.hilt.android.lifecycle.HiltViewModel
import com.ft.ltd.takeyourpill.utils.CallResult
import com.ft.ltd.takeyourpill.repository.HistoryRepository
import com.ft.ltd.takeyourpill.repository.PillRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class HistoryOverviewViewModel @Inject constructor(
    private val pillRepository: PillRepository,
    historyRepository: HistoryRepository
) : ViewModel() {

    val historyStats = historyRepository.getHistoryOrderedByIdFlow().map { history ->
        if (history.isEmpty()) {
            return@map (CallResult.failure<List<BaseModel>>())
        }

        val pills = pillRepository.getAllPillsIncludingDeleted()

        if (pills.isEmpty()) {
            return@map (CallResult.failure<List<BaseModel>>())
        }

        val statList = mutableListOf<StatItem>()

        val totalReminded = history.size
        val totalConfirmed = history.count { it.hasBeenConfirmed }
        val totalMissed = totalReminded - totalConfirmed

        val overallStat = StatItem(null, totalReminded, totalConfirmed, totalMissed)

        val pillsHistory = history.groupBy { it.pillId }.values
        // Iterate over each pill
        pillsHistory.forEach { pillHistory ->

            val pillId = pillHistory.first().pillId
            val pillReminded = pillHistory.size
            val pillConfirmed = pillHistory.count { it.hasBeenConfirmed }
            val pillMissed = pillReminded - pillConfirmed

            statList.add(StatItem(pillId, pillReminded, pillConfirmed, pillMissed))
        }

        val mergedList = sequence {
            yield(HistoryPillItem(HistoryOverallItem(), overallStat))
            pills.forEach { pill ->
                pill.itemType = BaseModel.ItemType.HISTORY
                statList.find { statItem -> statItem.pillId == pill.id }?.let { statItem ->
                    yield(HistoryPillItem(pill, statItem))
                }
            }
        }.toList()

        return@map (CallResult.success(mergedList))
    }.asLiveData()
}