package com.imm.operationsorchestrationmobile.service

import android.content.Context
import android.content.SharedPreferences
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.imm.operationsorchestrationmobile.activity.HostsActivity
import com.imm.operationsorchestrationmobile.adapter.ExecutionSummaryAdapter
import com.imm.operationsorchestrationmobile.adapter.FlowStatisticsAdapter
import com.imm.operationsorchestrationmobile.adapter.RunFlowAdapter
import com.imm.operationsorchestrationmobile.domain.Execution
import com.imm.operationsorchestrationmobile.domain.FlowInput
import com.imm.operationsorchestrationmobile.domain.RunExecution
import com.imm.operationsorchestrationmobile.domain.Step
import com.imm.operationsorchestrationmobile.util.DialogUtils
import com.imm.operationsorchestrationmobile.util.IntentUtils
import com.imm.operationsorchestrationmobile.util.bean.Dir
import com.imm.operationsorchestrationmobile.util.bean.File
import com.imm.operationsorchestrationmobile.util.binder.DirectoryNodeBinder
import com.imm.operationsorchestrationmobile.util.binder.FileNodeBinder
import org.json.JSONArray
import tellh.com.recyclertreeview_lib.TreeNode
import tellh.com.recyclertreeview_lib.TreeViewAdapter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class OOApiService {
    companion object {
        fun getFlowInputs(parentActivityContext: Context, context: Context, flowId: String, hostname: String, username: String, password: String) {
            val queue = Volley.newRequestQueue(context)
            val url = "https://" + hostname + ":8443/oo/rest/v2/flows/" + flowId + "/inputs"

            val stringRequest = object : StringRequest(Request.Method.GET, url,
                    Response.Listener<String> { response ->
                        val listOfInputs = getListOfInputsFromResponse(response.toString())
                        DialogUtils.createAndShowCustomDialog(context, listOfInputs, flowId, hostname, username, password)
                    },
                    Response.ErrorListener { error ->
                        Log.d("Volley error: ", error.message.toString())
                        Toast.makeText(context, "Cannot retrieve flow inputs",
                                Toast.LENGTH_SHORT).show()
                        IntentUtils.navigateToActivityNoHistory(parentActivityContext, HostsActivity::class)
                    }) {
                override fun getHeaders(): MutableMap<String, String> {
                    return getAuthorizationHeader(username, password)
                }

                override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
                    val token = response!!.headers.getOrDefault("X-CSRF-TOKEN", "invalid")
                    if (!token.equals("invalid")) {
                        updateLocalCsrfToken(context, token)
                    }
                    return super.parseNetworkResponse(response)
                }
            }
            queue.add(stringRequest)
        }

        fun getFlowExecutions(parentActivityContext: Context, context: Context, hostname: String, username: String, password: String, rv: RecyclerView, rm: RecyclerView.LayoutManager) {
            val queue = Volley.newRequestQueue(context)
            val url = "https://" + hostname + ":8443/oo/rest/v2/flows/library"
            val nodes = ArrayList<TreeNode<*>>()

            val stringRequest = object : StringRequest(Request.Method.GET, url,
                    Response.Listener<String> { response ->
                        completeNodesTreeFromResponse(response.toString(), nodes)
                        initializeTreeView(parentActivityContext, context, hostname, username, password, nodes, rv, rm)
                    },
                    Response.ErrorListener { error ->
                        Log.d("Volley error: ", error.message.toString())
                        Toast.makeText(context, "Cannot retrieve flow executions",
                                Toast.LENGTH_SHORT).show()
                        IntentUtils.navigateToActivityNoHistory(parentActivityContext, HostsActivity::class)
                    }) {
                override fun getHeaders(): MutableMap<String, String> {
                    return getAuthorizationHeader(username, password)
                }

                override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
                    val token = response!!.headers.getOrDefault("X-CSRF-TOKEN", "invalid")
                    if (!token.equals("invalid")) {
                        updateLocalCsrfToken(context, token)
                    }
                    return super.parseNetworkResponse(response)
                }
            }

            queue.add(stringRequest)
        }

        fun getStatistics(parentActivityContext: Context, context: Context, adapter: FlowStatisticsAdapter, flowPathList: MutableList<Execution>, hostname: String, username: String, password: String) {
            val queue = Volley.newRequestQueue(context)
            val url = "https://" + hostname + ":8443/oo/rest/v2/executions/statistics"

            flowPathList.clear()

            val stringRequest = object : StringRequest(Request.Method.GET, url,
                    Response.Listener<String> { response ->
                        addExecutionsToList(flowPathList, response.toString())
                        adapter.notifyDataSetChanged()
                    },
                    Response.ErrorListener { error ->
                        Log.d("Volley error: ", error.message.toString())
                        Toast.makeText(context, "Cannot connect to Central host",
                                Toast.LENGTH_SHORT).show()
                        IntentUtils.navigateToActivityNoHistory(parentActivityContext, HostsActivity::class)
                    }) {
                override fun getHeaders(): MutableMap<String, String> {
                    return getAuthorizationHeader(username, password)
                }

                override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
                    val token = response!!.headers.getOrDefault("X-CSRF-TOKEN", "invalid")
                    if (!token.equals("invalid")) {
                        updateLocalCsrfToken(context, token)
                    }
                    return super.parseNetworkResponse(response)
                }
            }

            queue.add(stringRequest)
        }

        fun getTotalRoi(parentActivityContext: Context, context: Context, fab: FloatingActionButton, hostname: String, username: String, password: String) {
            val queue = Volley.newRequestQueue(context)
            val url = "https://" + hostname + ":8443/oo/rest/v2/executions/statistics"

            val stringRequest = object : StringRequest(Request.Method.GET, url,
                    Response.Listener<String> { response ->
                        sumRoi(fab, response.toString())
                    },
                    Response.ErrorListener { error ->
                        Log.d("Volley error: ", error.message.toString())
                        Toast.makeText(context, "Cannot connect to Central host",
                                Toast.LENGTH_SHORT).show()
                        IntentUtils.navigateToActivityNoHistory(parentActivityContext, HostsActivity::class)
                    }) {
                override fun getHeaders(): MutableMap<String, String> {
                    return getAuthorizationHeader(username, password)
                }

                override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
                    val token = response!!.headers.getOrDefault("X-CSRF-TOKEN", "invalid")
                    if (!token.equals("invalid")) {
                        updateLocalCsrfToken(context, token)
                    }
                    return super.parseNetworkResponse(response)
                }
            }

            queue.add(stringRequest)
        }

        fun getExecutions(parentActivityContext: Context, context: Context, adapter: RunFlowAdapter, runExplorerList: MutableList<RunExecution>, hostname: String, username: String, password: String) {
            val queue = Volley.newRequestQueue(context)
            val url = "https://" + hostname + ":8443/oo/rest/v2/executions"

            runExplorerList.clear()

            val stringRequest = object : StringRequest(Request.Method.GET, url,
                    Response.Listener<String> { response ->
                        addFlowsToList(runExplorerList, response.toString())
                        adapter.notifyDataSetChanged()
                    },
                    Response.ErrorListener { error ->
                        Log.d("Volley error: ", error.message.toString())
                        Toast.makeText(context, "Cannot retrieve statistics",
                                Toast.LENGTH_SHORT).show()
                        IntentUtils.navigateToActivityNoHistory(parentActivityContext, HostsActivity::class)
                    }) {
                override fun getHeaders(): MutableMap<String, String> {
                    return getAuthorizationHeader(username, password)
                }

                override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
                    val token = response!!.headers.getOrDefault("X-CSRF-TOKEN", "invalid")
                    if (!token.equals("invalid")) {
                        updateLocalCsrfToken(context, token)
                    }
                    return super.parseNetworkResponse(response)
                }
            }

            queue.add(stringRequest)
        }

        fun getExecutionSteps(parentActivityContext: Context, context: Context, adapter: ExecutionSummaryAdapter, stepList: MutableList<Step>, flowId: String, hostname: String, username: String, password: String) {
            val queue = Volley.newRequestQueue(context)
            val url = "https://" + hostname + ":8443/oo/rest/v2/executions/" + flowId + "/steps"

            stepList.clear()

            val stringRequest = object : StringRequest(Request.Method.GET, url,
                    Response.Listener<String> { response ->
                        addStepsToList(stepList, response.toString())
                        adapter.notifyDataSetChanged()
                    },
                    Response.ErrorListener { error ->
                        Log.d("Volley error: ", error.message.toString())
                        Toast.makeText(context, "Cannot retrieve statistics",
                                Toast.LENGTH_SHORT).show()
                        IntentUtils.navigateToActivityNoHistory(parentActivityContext, HostsActivity::class)
                    }) {
                override fun getHeaders(): MutableMap<String, String> {
                    return getAuthorizationHeader(username, password)
                }

                override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
                    val token = response!!.headers.getOrDefault("X-CSRF-TOKEN", "invalid")
                    if (!token.equals("invalid")) {
                        updateLocalCsrfToken(context, token)
                    }
                    return super.parseNetworkResponse(response)
                }
            }

            queue.add(stringRequest)
        }

        fun sendExecutionRequest(context: Context, hostname: String, username: String, password: String, flowId: String) {
            val queue = Volley.newRequestQueue(context)
            val url = "https://" + hostname + ":8443/oo/rest/v2/executions"

            val stringRequest = object : StringRequest(Request.Method.POST, url,
                    Response.Listener<String> { response ->
                        Log.d("Volley response: ", response.toString())
                        Toast.makeText(context, "Flow execution successfully started",
                                Toast.LENGTH_SHORT).show()
                    },
                    Response.ErrorListener { error ->
                        Log.d("Volley error: ", error.message.toString())
                        Toast.makeText(context, "Cannot execute flow",
                                Toast.LENGTH_SHORT).show()
                    }) {
                override fun getHeaders(): MutableMap<String, String> {
                    return getAuthorizationHeader(username, password)
                }

                override fun getBody(): ByteArray {
                    return createJsonFromInputs(flowId).toByteArray()
                }

                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }
            }

            queue.add(stringRequest)
        }

        fun createJsonFromInputs(flowUuid: String): String {
            var returnString = "{\"flowUuid\":\"" + flowUuid + "\",\"inputs\":{"
            for (key in DialogUtils.editTextMap.keys) {
                returnString = returnString + "\"" + key + "\":\"" + DialogUtils.editTextMap[key]!!.text.toString() + "\","
            }
            Log.d("JsonFromInputs: ", returnString.substring(0, returnString.length - 1) + "}}")
            return returnString.substring(0, returnString.length - 1) + "}}";
        }

        fun sumRoi(fab: FloatingActionButton, response: String) {
            Log.d("Volley response: ", response)
            var totalRoi = 0
            val jsonArray = JSONArray(response)

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                totalRoi += jsonObject.getInt("flowRoi")
                Log.d("JSON Object: ", jsonObject.toString())
            }

            fab.setOnClickListener { view ->
                Snackbar.make(view, "Total ROI: " + totalRoi, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
            }
        }

        fun addExecutionsToList(executionList: MutableList<Execution>, response: String) {
            Log.d("Volley response: ", response)
            val jsonArray = JSONArray(response)

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val flowId = jsonObject.getString("flowUuid")
                val flowName = jsonObject.getString("flowPath")
                val flowRoi = jsonObject.getString("flowRoi")
                val numberOfExecutions = jsonObject.getString("numberOfExecutions")
                val averageExecutionTime = jsonObject.getString("averageExecutionTime")
                val resultsArray = JSONArray(jsonObject.getString("resultsDistribution"))
                val type = resultsArray.getJSONObject(0).getString("type")
                val finalName = flowName.substring(flowName.lastIndexOf('/') + 1, flowName.indexOf('.'))
                val hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(averageExecutionTime.toLong()),
                        TimeUnit.MILLISECONDS.toMinutes(averageExecutionTime.toLong()) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(averageExecutionTime.toLong()) % TimeUnit.MINUTES.toSeconds(1))
                executionList.add(Execution(flowId, finalName, flowRoi, numberOfExecutions, hms, type))
                Log.d("JSON Object: ", jsonObject.toString())
            }
        }

        fun addFlowsToList(flowPathList: MutableList<RunExecution>, response: String) {
            Log.d("Volley response: ", response)
            val jsonArray = JSONArray(response)

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val executionId = jsonObject.getString("executionId")
                val executionName = jsonObject.getString("executionName")
                val status = jsonObject.getString("resultStatusType")
                val roi = jsonObject.getString("roi")
                val owner = jsonObject.getString("owner")
                if(roi.equals("null") || roi == null)
                    flowPathList.add(RunExecution(executionId, executionName, status, "-", owner))
                else
                    flowPathList.add(RunExecution(executionId, executionName, status, roi, owner))
                Log.d("JSON Object: ", jsonObject.toString())
            }
        }

        fun addStepsToList(stepList: MutableList<Step>, response: String) {
            Log.d("Volley response: ", response)
            val jsonArray = JSONArray(response)

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val stepInfoObject = jsonObject.getJSONObject("stepInfo")
                val stepName = stepInfoObject.getString("stepName")
                val status = stepInfoObject.getString("responseType")
                val stepPrimaryResult = jsonObject.getString("stepPrimaryResult")
                if (stepPrimaryResult != null)
                    stepList.add(Step(stepName, stepPrimaryResult, status))
                else
                    stepList.add(Step(stepName, stepPrimaryResult, ""))
                Log.d("JSON Object: ", jsonObject.toString())
            }
        }

        fun initializeTreeView(parentActivityContext: Context, context: Context, hostname: String, username: String, password: String, nodes: ArrayList<TreeNode<*>>, rv: RecyclerView, rm: RecyclerView.LayoutManager) {
            val adapter = TreeViewAdapter(nodes, Arrays.asList(DirectoryNodeBinder(), FileNodeBinder()))
            rv.layoutManager = rm
            rv.adapter = adapter
            adapter.setOnTreeNodeListener(object : TreeViewAdapter.OnTreeNodeListener {
                override fun onClick(node: TreeNode<*>, holder: RecyclerView.ViewHolder): Boolean {
                    if (!node.isLeaf) {
                        onToggle(!node.isExpand, holder)
                    } else {
                        val flowToExecute = node.content as File
                        getFlowInputs(parentActivityContext, context, flowToExecute.id, hostname, username, password)
                    }
                    return false
                }

                override fun onToggle(isExpand: Boolean, holder: RecyclerView.ViewHolder) {
                    val dirViewHolder = holder as DirectoryNodeBinder.ViewHolder
                    val ivArrow = dirViewHolder.ivArrow
                    val rotateDegree = if (isExpand) 90 else -90
                    ivArrow.animate().rotationBy(rotateDegree.toFloat()).start()
                }
            })
        }

        fun completeNodesTreeFromResponse(response: String, nodes: ArrayList<TreeNode<*>>) {
            val nodesTree = mutableMapOf<String, TreeNode<*>>()
            val jsonArray = JSONArray(response)

            Log.d("Volley response: ", response)

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val objectName = jsonObject.getString("name")
                val path = jsonObject.getString("path")

                if (jsonObject.getString("parentId") == "null") {
                    val rootNode = TreeNode(Dir(objectName))
                    nodesTree[path] = rootNode
                    nodes.add(rootNode)
                } else {
                    val id = jsonObject.getString("id")
                    val parentId = jsonObject.getString("parentId")
                    val leaf = jsonObject.getString("leaf")
                    val runnable = jsonObject.getBoolean("runnable")
                    var currentNode: TreeNode<*>

                    if (leaf == "false")
                        currentNode = TreeNode(Dir(objectName))
                    else
                        currentNode = TreeNode(File(objectName, id, runnable))

                    nodesTree[parentId]!!.addChild(currentNode)
                    nodesTree[path] = currentNode
                }
                Log.d("JSON Object: ", jsonObject.toString())
            }
        }

        fun getListOfInputsFromResponse(response: String): ArrayList<FlowInput> {
            val listOfInputs: ArrayList<FlowInput> = ArrayList()
            val jsonArray = JSONArray(response)

            Log.d("Volley response: ", response)

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val name = jsonObject.getString("name")
                val required = jsonObject.getBoolean("mandatory")
                listOfInputs.add(FlowInput(name, name, required))
                Log.d("JSON Object: ", jsonObject.toString())
            }
            return listOfInputs
        }

        fun getAuthorizationHeader(username: String, password: String): HashMap<String, String> {
            val headers = HashMap<String, String>()
            val creds = String.format("%s:%s", username, password)
            val auth = "Basic " + Base64.encodeToString(creds.toByteArray(), Base64.DEFAULT)
            headers.put("Content-Type", "application/json")
            headers.put("Authorization", auth)
            return headers
        }

        fun updateLocalCsrfToken(context: Context, token: String) {
            val prefs: SharedPreferences? = context.getSharedPreferences("access_token", 0)
            val editor = prefs!!.edit()
            editor.putString("X-CSRF-TOKEN", token)
            editor.apply()
        }
    }
}