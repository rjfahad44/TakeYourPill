package com.ft.ltd.takeyourpill.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ft.ltd.takeyourpill.ad_manager.admob.AdMobAdsManager
import com.ft.ltd.takeyourpill.ad_manager.ads_viewholder.AdsViewHolder
import com.ft.ltd.takeyourpill.model.BaseModel
import com.ft.ltd.takeyourpill.ad_manager.model.AdsItem
import com.ft.ltd.takeyourpill.adapter.viewholder.*
import com.ft.ltd.takeyourpill.databinding.*
import com.ft.ltd.takeyourpill.model.BaseModel.ItemType
import com.ft.ltd.takeyourpill.model.ChartItem
import com.ft.ltd.takeyourpill.model.History
import com.ft.ltd.takeyourpill.model.Pill
import com.ft.ltd.takeyourpill.model.HeaderItem
import com.ft.ltd.takeyourpill.model.HistoryPillItem
import com.ft.ltd.takeyourpill.model.EmptyItem

private const val TAG = "AppRecyclerAdapter"
class AppRecyclerAdapter(
    private val context: Context,
    private val headerText: String?,
    private val emptyDescription: String,
    private val emptyDrawable: Drawable?,
    private val adMobAdsManager: AdMobAdsManager
) : ListAdapter<BaseModel, RecyclerView.ViewHolder>(BaseModel.DiffCallback) {

    private var onItemClickListener: (View, BaseModel) -> Unit = { _, _ -> }
    private var onPillConfirmClickListener: (History) -> Unit = { }
    private var adsShowPosition: Int = 1

    fun setOnItemClickListener(listener: (View, BaseModel) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnPillConfirmClickListener(listener: (History) -> Unit) {
        onPillConfirmClickListener = listener
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ItemType.PILL.ordinal -> if (holder is PillViewHolder) holder.bind(
                getItem(position) as Pill
            )
            ItemType.HEADER.ordinal -> if (holder is HeaderViewHolder) holder.bind(
                (getItem(position) as HeaderItem).title
            )
            ItemType.HISTORY.ordinal -> if (holder is HistoryViewHolder) holder.bind(
                getItem(position) as HistoryPillItem
            )
            ItemType.CHART.ordinal -> if (holder is ChartViewHolder) holder.bind(
                getItem(position) as ChartItem
            )
            ItemType.EMPTY.ordinal -> if (holder is EmptyViewHolder) holder.bind()

            ItemType.ADS_VIEW.ordinal -> if (holder is AdsViewHolder) holder.bind()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ItemType.PILL.ordinal -> PillViewHolder(
                ItemPillBinding.inflate(layoutInflater, parent, false),
                onItemClickListener,
                onPillConfirmClickListener,
            )
            ItemType.HEADER.ordinal -> HeaderViewHolder(
                ItemHeaderBinding.inflate(layoutInflater, parent, false)
            )
            ItemType.EMPTY.ordinal -> EmptyViewHolder(
                LayoutViewEmptyBinding.inflate(layoutInflater, parent, false),
                emptyDescription,
                emptyDrawable
            )
            ItemType.HISTORY.ordinal -> HistoryViewHolder(
                ItemHistoryPillBinding.inflate(layoutInflater, parent, false),
                onItemClickListener
            )
            ItemType.CHART.ordinal -> ChartViewHolder(
                ItemChartBinding.inflate(layoutInflater, parent, false),
            )
            ItemType.ADS_VIEW.ordinal -> AdsViewHolder(
                context,
                RowAdsLayoutBinding.inflate(layoutInflater, parent, false),
                adMobAdsManager
            )
            else -> throw RuntimeException("Unknown ViewHolder")
        }
    }

    override fun submitList(list: List<BaseModel>?) = submitList(list, null)

    override fun submitList(list: List<BaseModel>?, commitCallback: Runnable?) {
        list?.let { ll ->
            val newList = ll.toMutableList()
            headerText?.let { newList.add(0, HeaderItem(it)) }
            if (ll.isEmpty()) {
                newList.add(EmptyItem())
            }else{
                Log.d(TAG, "list size: ${newList.size}")
                // ads add into the list item //
//                var pos = 1
//                for (i in 1 ..newList.size+1){
//                    if (pos == 2){
//                        newList.add(i, AdsItem())
//                        pos = -1
//                    }
//                    pos++
//                }
            }
            super.submitList(newList, commitCallback)
        } ?: super.submitList(list, commitCallback)
    }

    override fun getItemViewType(position: Int) = getItem(position).itemType.ordinal
}