package com.imm.operationsorchestrationmobile.util.binder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.imm.operationsorchestrationmobile.R
import com.imm.operationsorchestrationmobile.util.bean.Dir
import tellh.com.recyclertreeview_lib.TreeNode
import tellh.com.recyclertreeview_lib.TreeViewBinder


class DirectoryNodeBinder : TreeViewBinder<DirectoryNodeBinder.ViewHolder>() {
    override fun provideViewHolder(itemView: View): ViewHolder {
        return ViewHolder(itemView)
    }

    override fun bindView(holder: ViewHolder, position: Int, node: TreeNode<*>) {
        holder.ivArrow.setRotation(0F)
        holder.ivArrow.setImageResource(R.drawable.ic_keyboard_arrow_right_black_18dp)
        val rotateDegree = if (node.isExpand) 90 else 0
        holder.ivArrow.setRotation(rotateDegree.toFloat())
        val dirNode = node.content as Dir
        holder.tvName.text = dirNode.dirName
        if (node.isLeaf)
            holder.ivArrow.setVisibility(View.INVISIBLE)
        else
            holder.ivArrow.setVisibility(View.VISIBLE)
    }

    override fun getLayoutId(): Int {
        return R.layout.item_dir
    }

    class ViewHolder(rootView: View) : TreeViewBinder.ViewHolder(rootView) {
        val ivArrow: ImageView
        val tvName: TextView

        init {
            this.ivArrow = rootView.findViewById<View>(R.id.iv_arrow) as ImageView
            this.tvName = rootView.findViewById<View>(R.id.tv_name) as TextView
        }
    }
}