package com.imm.operationsorchestrationmobile.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.imm.operationsorchestrationmobile.R
import com.imm.operationsorchestrationmobile.adapter.ExecutionSummaryAdapter
import com.imm.operationsorchestrationmobile.domain.Step
import com.imm.operationsorchestrationmobile.service.OOApiService.Companion.getExecutionSteps
import kotlinx.android.synthetic.main.app_bar_host.*
import kotlinx.android.synthetic.main.content_execution_summary.*

class ExecutionSummary : AppCompatActivity() {
    var flowName: String = ""
    var flowId: String = ""
    var hostname: String = ""
    var username: String = ""
    var password: String = ""
    var stepList: MutableList<Step> = ArrayList<Step>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_execution_summary)

        setSupportActionBar(toolbar)

        val intent = intent
        flowName = intent.getStringExtra("flowName")
        flowId = intent.getStringExtra("flowId")
        hostname = intent.getStringExtra("hostname")
        username = intent.getStringExtra("username")
        password = intent.getStringExtra("password")

        supportActionBar!!.title = flowName + "  - Summary"

        val myRecyclerView: RecyclerView = findViewById(R.id.executionSummaryRecyclerView);
        val myRecyclerAdapter = ExecutionSummaryAdapter(stepList, this);
        val myRecyclerManager = LinearLayoutManager(this);

        myRecyclerAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                if (myRecyclerAdapter.getItemCount() == 0) {
                    executionSummaryEmptyMessage.visibility = View.VISIBLE
                    executionSummaryRecyclerView.visibility = View.GONE
                } else {
                    executionSummaryEmptyMessage.visibility = View.GONE
                    executionSummaryRecyclerView.visibility = View.VISIBLE
                }
            }
        })

        myRecyclerView.layoutManager = myRecyclerManager;
        myRecyclerView.adapter = myRecyclerAdapter;
        myRecyclerView.setHasFixedSize(true);

        getExecutionSteps(applicationContext, applicationContext, myRecyclerAdapter, stepList, flowId, hostname, username, password)
    }
}
