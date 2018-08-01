package com.imm.operationsorchestrationmobile.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.imm.operationsorchestrationmobile.R
import com.imm.operationsorchestrationmobile.domain.Execution
import kotlinx.android.synthetic.main.execution_row.view.*

class FlowStatisticsAdapter(private val flowExecutionList: MutableList<Execution>,
                            private val context: Context) : RecyclerView.Adapter<FlowStatisticsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.execution_row, parent, false)
        return ViewHolder(view, context)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindHost(flowExecutionList.get(position))
    }

    override fun getItemCount() = flowExecutionList.size


    class ViewHolder(val containerView: View, val context: Context)
        : RecyclerView.ViewHolder(containerView) {

        fun bindHost(execution: Execution) {
            with(execution) {
                containerView.execTimeLayout.text = "Execution time: " + execution.executionTime
                containerView.flowNameLayout.text = execution.flowName
                containerView.execNumberLayout.text = "Number of executions: " + execution.numberOfExecutions
                if (execution.status.contains("resolved", true))
                    containerView.executionIcon.setBackgroundResource(R.drawable.checked)
                else if (execution.status.contains("error", true))
                    containerView.executionIcon.setBackgroundResource(R.drawable.cancel)
                else
                    containerView.executionIcon.setBackgroundResource(R.drawable.hierarhical_structure)
            }
        }
    }
}