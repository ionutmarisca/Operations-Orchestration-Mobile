package com.imm.operationsorchestrationmobile.fragment

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.imm.operationsorchestrationmobile.R
import com.imm.operationsorchestrationmobile.service.OOApiService
import com.imm.operationsorchestrationmobile.service.OOApiService.Companion.getFlowExecutions
import tellh.com.recyclertreeview_lib.TreeNode

private const val ARG_HOSTNAME = "hostname"
private const val ARG_USERNAME = "username"
private const val ARG_PASSWORD = "password"

class FlowExecutionFragment : Fragment() {
    private var hostname: String? = null
    private var username: String? = null
    private var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            hostname = it.getString(ARG_HOSTNAME)
            username = it.getString(ARG_USERNAME)
            password = it.getString(ARG_PASSWORD)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar!!.title = "Flow Execution"
        return inflater.inflate(R.layout.fragment_flow_execution, container, false)
    }

    override fun onStart() {
        super.onStart()
        val rv: RecyclerView = view!!.findViewById(R.id.rv)
        val rm = LinearLayoutManager(context);
        getFlowExecutions(activity?.applicationContext!!, context!!, hostname!!, username!!, password!!, rv, rm)
    }

    override fun onDetach() {
        super.onDetach()
    }

    companion object {
        @JvmStatic
        fun newInstance(hostname: String, username: String, password: String) =
                FlowExecutionFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_HOSTNAME, hostname)
                        putString(ARG_USERNAME, username)
                        putString(ARG_PASSWORD, password)
                    }
                }
    }
}
