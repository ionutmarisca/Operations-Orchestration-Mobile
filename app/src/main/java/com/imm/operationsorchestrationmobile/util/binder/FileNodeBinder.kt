package com.imm.operationsorchestrationmobile.util.binder

import android.view.View
import android.widget.TextView
import com.imm.operationsorchestrationmobile.R
import com.imm.operationsorchestrationmobile.util.bean.File
import tellh.com.recyclertreeview_lib.TreeNode
import tellh.com.recyclertreeview_lib.TreeViewBinder


class FileNodeBinder : TreeViewBinder<FileNodeBinder.ViewHolder>() {
    override fun provideViewHolder(itemView: View): ViewHolder {
        return ViewHolder(itemView)
    }

    override fun bindView(holder: ViewHolder, position: Int, node: TreeNode<*>) {
        val fileNode = node.getContent() as File
        holder.tvName.setText(fileNode.fileName)
    }

    override fun getLayoutId(): Int {
        return R.layout.item_file
    }

    inner class ViewHolder(rootView: View) : TreeViewBinder.ViewHolder(rootView) {
        var tvName: TextView

        init {
            this.tvName = rootView.findViewById(R.id.tv_name)
        }

    }
}