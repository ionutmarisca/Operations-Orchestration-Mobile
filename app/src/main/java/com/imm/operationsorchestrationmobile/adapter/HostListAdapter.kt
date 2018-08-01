package com.imm.operationsorchestrationmobile.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alexandrius.accordionswipelayout.library.SwipeLayout
import com.google.firebase.database.DatabaseReference
import com.imm.operationsorchestrationmobile.R
import com.imm.operationsorchestrationmobile.activity.HostActivity
import com.imm.operationsorchestrationmobile.domain.Host
import com.imm.operationsorchestrationmobile.repository.HostRepository
import kotlinx.android.synthetic.main.add_dialog.view.*
import kotlinx.android.synthetic.main.hosts_row.view.*
import kotlinx.android.synthetic.main.login_dialog.view.*
import kotlinx.android.synthetic.main.swipe.view.*


/**
 * Created by ionutmarisca on 02/05/2018.
 */
class HostListAdapter(private val hostRepository: HostRepository,
                      private val myRef: DatabaseReference,
                      private val context: Context) : RecyclerView.Adapter<HostListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.swipe, parent, false)
        return ViewHolder(view, context)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindHost(hostRepository.getHost(position))
        val swipeLayout = holder.containerView.swipe_layout as SwipeLayout
        holder.containerView.setOnClickListener {
            val myBuilder = AlertDialog.Builder(context)
            val myView = LayoutInflater.from(context).inflate(R.layout.login_dialog, null)

            myBuilder.setView(myView)

            myBuilder.setTitle("Central Login").setCancelable(true).setPositiveButton("Login") { dialog, whichButton ->
                val intent = Intent(context, HostActivity::class.java)
                intent.putExtra("displayName", hostRepository.getHost(position).displayName)
                intent.putExtra("hostname", hostRepository.getHost(position).hostname)
                intent.putExtra("username", myView.loginUsername.text.toString())
                intent.putExtra("password", myView.loginPassword.text.toString())
                context.startActivity(intent)
            }

            val dialog = myBuilder.create()
            dialog.show()
        }
        swipeLayout.setOnSwipeItemClickListener { left, index ->
            if (left) {
                when (index) {
                    0 -> {
                    }
                }
            } else {
                when (index) {
                    0 -> {
                        val myBuilder = AlertDialog.Builder(context)
                        val myView = LayoutInflater.from(context).inflate(R.layout.add_dialog, null)

                        myView.addDisplayName.setText(hostRepository.getHost(position).displayName)
                        myView.addHostname.setText(hostRepository.getHost(position).hostname)
                        myView.checkboxSecure.isChecked = hostRepository.getHost(position).isSecure

                        myBuilder.setView(myView)
                        myBuilder.setOnCancelListener(DialogInterface.OnCancelListener {
                            swipeLayout.collapseAll(true)
                        })
                        myBuilder.setTitle("Update host").setCancelable(true).setPositiveButton("Update") { dialog, whichButton ->
                            val host = Host(myView.addDisplayName.text.toString(), myView.addHostname.text.toString(), myView.checkboxSecure.isChecked)

                            hostRepository.updateHost(position, host)
                            notifyItemChanged(position)
                            myRef.setValue(hostRepository)
                        }

                        val dialog = myBuilder.create()
                        dialog.show()
                    }
                    1 -> {
                        val myBuilder = AlertDialog.Builder(context)
                        val myView = LayoutInflater.from(context).inflate(R.layout.delete_dialog, null)

                        myBuilder.setView(myView)
                        myBuilder.setOnCancelListener(DialogInterface.OnCancelListener {
                            swipeLayout.collapseAll(true)
                        })
                        myBuilder.setTitle("Delete host")
                                .setCancelable(true)
                                .setPositiveButton("Delete") { dialog, whichButton ->
                                    hostRepository.deleteHost(position)
                                    myRef.setValue(hostRepository)
                                    notifyItemRemoved(position)
                                }
                                .setNegativeButton("Cancel") { dialog, whichButton ->
                                    dialog.dismiss()
                                    swipeLayout.collapseAll(true)
                                }

                        val dialog = myBuilder.create()
                        dialog.show()
                    }
                }
            }
        }

    }

    override fun getItemCount() = hostRepository.getHostsList().size


    class ViewHolder(val containerView: View, val context: Context)
        : RecyclerView.ViewHolder(containerView) {

        fun bindHost(host: Host) {
            with(host) {
                containerView.displayNameLayout.text = host.displayName
                containerView.hostnameLayout.text = host.hostname
                if (!host.isSecure)
                    containerView.icon.setBackgroundResource(R.drawable.host_unsecure)
                else
                    containerView.icon.setBackgroundResource(R.drawable.host_secure)
            }
        }
    }
}