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
import com.imm.operationsorchestrationmobile.adapter.RunFlowAdapter
import com.imm.operationsorchestrationmobile.domain.RunExecution
import com.imm.operationsorchestrationmobile.service.OOApiService
import kotlinx.android.synthetic.main.fragment_run_explorer.*

private const val ARG_HOSTNAME = "hostname"
private const val ARG_USERNAME = "username"
private const val ARG_PASSWORD = "password"

class RunExplorerFragment : Fragment() {
    private var hostname: String? = null
    private var username: String? = null
    private var password: String? = null
    private var runExplorerList: MutableList<RunExecution> = ArrayList<RunExecution>()

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

        val myRecyclerView: RecyclerView = view!!.findViewById(R.id.runExecutionsRecyclerView);
        val myRecyclerAdapter = RunFlowAdapter(runExplorerList, context!!, hostname!!, username!!, password!!);
        val myRecyclerManager = LinearLayoutManager(context!!);

        myRecyclerAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                if (myRecyclerAdapter.getItemCount() == 0) {
                    emptyRunMessage.visibility = View.VISIBLE
                    runExecutionsRecyclerView.visibility = View.GONE
                } else {
                    emptyRunMessage.visibility = View.GONE
                    runExecutionsRecyclerView.visibility = View.VISIBLE
                }
            }
        })

        myRecyclerView.layoutManager = myRecyclerManager;
        myRecyclerView.adapter = myRecyclerAdapter;
        myRecyclerView.setHasFixedSize(true);
        OOApiService.getExecutions(activity!!.applicationContext, context!!, myRecyclerAdapter, runExplorerList, hostname!!, username!!, password!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar!!.title = "Run Explorer"
        return inflater.inflate(R.layout.fragment_run_explorer, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(hostname: String, username: String, password: String) =
                RunExplorerFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_HOSTNAME, hostname)
                        putString(ARG_USERNAME, username)
                        putString(ARG_PASSWORD, password)
                    }
                }
    }
}