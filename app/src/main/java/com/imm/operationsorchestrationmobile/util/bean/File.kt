package com.imm.operationsorchestrationmobile.util.bean

import com.imm.operationsorchestrationmobile.R
import tellh.com.recyclertreeview_lib.LayoutItemType


class File(var fileName: String, var id: String, var runnable: Boolean) : LayoutItemType {

    override fun getLayoutId(): Int {
        return R.layout.item_file
    }
}