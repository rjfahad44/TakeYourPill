package com.ft.ltd.takeyourpill.model

import com.ft.ltd.takeyourpill.model.BaseModel

class HeaderItem(val title: String) : BaseModel() {
    override val itemType = ItemType.HEADER
    override fun isSame(newItem: BaseModel) = true
    override fun isContentSame(newItem: BaseModel) = true
}