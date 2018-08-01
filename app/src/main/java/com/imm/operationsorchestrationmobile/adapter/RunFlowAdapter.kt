package com.imm.operationsorchestrationmobile.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.imm.operationsorchestrationmobile.R
import com.imm.operationsorchestrationmobile.activity.ExecutionSummary
import com.imm.operationsorchestrationmobile.domain.RunExecution
import kotlinx.android.synthetic.main.run_execution_row.view.*

class RunFlowAdapter(private val runFlowList: MutableList<RunExecution>,
                     private val context: Context,
                     private val hostname: String,
                     private val username: String,
                     private val password: String) : RecyclerView.Adapter<RunFlowAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.run_execution_row, parent, false)
        return ViewHolder(view, context)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindHost(runFlowList.get(position))
        holder.containerView.runParent.setOnClickListener {
            val intent = Intent(context, ExecutionSummary::class.java)
            intent.putExtra("flowName", runFlowList.get(position).executionName)
            intent.putExtra("flowId", runFlowList.get(position).executionId)
            intent.putExtra("hostname", hostname)
            intent.putExtra("username", username)
            intent.putExtra("password", password)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = runFlowList.size


    class ViewHolder(val containerView: View, val context: Context)
        : RecyclerView.ViewHolder(containerView) {

        fun bindHost(runExecution: RunExecution) {
            with(runExecution) {
                containerView.ownerLayout.text = "Owner: " + runExecution.owner
                containerView.runFlowNameLayout.text = runExecution.executionName
                containerView.roiLayout.text = "ROI: " + runExecution.roi
                if (runExecution.status.contains("resolved", true))
                    containerView.runExecutionIcon.setBackgroundResource(R.drawable.checked)
                else if (runExecution.status.contains("error", true))
                    containerView.runExecutionIcon.setBackgroundResource(R.drawable.cancel)
                else
                    containerView.runExecutionIcon.setBackgroundResource(R.drawable.hierarhical_structure)
            }
        }
    }
}