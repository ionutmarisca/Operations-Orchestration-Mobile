package com.imm.operationsorchestrationmobile.util.bean

import com.imm.operationsorchestrationmobile.R
import tellh.com.recyclertreeview_lib.LayoutItemType


class Dir(var dirName: String) : LayoutItemType {

    override fun getLayoutId(): Int {
        return R.layout.item_dir
    }
}