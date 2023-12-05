package com.ft.ltd.takeyourpill.ad_manager.model

import com.ft.ltd.takeyourpill.model.BaseModel

class AdsItem : BaseModel() {
    override val itemType = ItemType.ADS_VIEW
    override fun isSame(newItem: BaseModel) = false
    override fun isContentSame(newItem: BaseModel) = false
}