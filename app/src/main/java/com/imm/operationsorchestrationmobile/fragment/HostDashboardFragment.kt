package com.imm.operationsorchestrationmobile.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.imm.operationsorchestrationmobile.R
import com.imm.operationsorchestrationmobile.adapter.FlowStatisticsAdapter
import com.imm.operationsorchestrationmobile.domain.Execution
import com.imm.operationsorchestrationmobile.service.OOApiService.Companion.getStatistics
import kotlinx.android.synthetic.main.fragment_host_dashboard.*

private const val ARG_HOSTNAME = "hostname"
private const val ARG_USERNAME = "username"
private const val ARG_PASSWORD = "password"

class HostDashboardFragment : Fragment() {
    private var hostname: String? = null
    private var username: String? = null
    private var password: String? = null
    private var executionList: MutableList<Execution> = ArrayList<Execution>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            hostname = it.getString(ARG_HOSTNAME)
            username = it.getString(ARG_USERNAME)
            password = it.getString(ARG_PASSWORD)
        }
    }

    override fun onStart() {
        super.onStart()

        val myRecyclerView: RecyclerView = view!!.findViewById(R.id.statisticsRecyclerView);
        val myRecyclerAdapter = FlowStatisticsAdapter(executionList, context!!);
        val myRecyclerManager = LinearLayoutManager(context!!);

        myRecyclerAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                if (myRecyclerAdapter.getItemCount() == 0) {
                    emptyStatisticsMessage.visibility = View.VISIBLE
                    statisticsRecyclerView.visibility = View.GONE
                } else {
                    emptyStatisticsMessage.visibility = View.GONE
                    statisticsRecyclerView.visibility = View.VISIBLE
                }
            }
        })

        myRecyclerView.layoutManager = myRecyclerManager;
        myRecyclerView.adapter = myRecyclerAdapter;
        myRecyclerView.setHasFixedSize(true);
        getStatistics(activity!!.applicationContext, context!!, myRecyclerAdapter, executionList, hostname!!, username!!, password!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar!!.title = "Dashboard"
        return inflater.inflate(R.layout.fragment_host_dashboard, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(hostname: String, username: String, password: String) =
                HostDashboardFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_HOSTNAME, hostname)
                        putString(ARG_USERNAME, username)
                        putString(ARG_PASSWORD, password)
                    }
                }
    }
}