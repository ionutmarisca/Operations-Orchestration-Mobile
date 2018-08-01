package com.imm.operationsorchestrationmobile.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.imm.operationsorchestrationmobile.R
import com.imm.operationsorchestrationmobile.domain.Step
import kotlinx.android.synthetic.main.execution_summary_row.view.*

class ExecutionSummaryAdapter(private val executionSummaryList: MutableList<Step>,
                              private val context: Context) : RecyclerView.Adapter<ExecutionSummaryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.execution_summary_row, parent, false)
        return ViewHolder(view, context)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindHost(executionSummaryList.get(position))
    }

    override fun getItemCount() = executionSummaryList.size


    class ViewHolder(val containerView: View, val context: Context)
        : RecyclerView.ViewHolder(containerView) {

        fun bindHost(step: Step) {
            with(step) {
                containerView.stepNameLayout.text = step.stepName
                if (!step.stepResult.isEmpty() && !step.stepResult.equals("null"))
                    containerView.stepResultLayout.text = "Step result: " + step.stepResult
                else
                    containerView.stepResultLayout.text = "No step result"
                if (step.status.contains("resolved", true))
                    containerView.stepIcon.setBackgroundResource(R.drawable.checked)
                else if (step.status.contains("error", true))
                    containerView.stepIcon.setBackgroundResource(R.drawable.cancel)
                else
                    containerView.stepIcon.setBackgroundResource(R.drawable.hierarhical_structure)
            }
        }
    }
}