package com.imm.operationsorchestrationmobile.activity

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.imm.operationsorchestrationmobile.R
import com.imm.operationsorchestrationmobile.adapter.HostListAdapter
import com.imm.operationsorchestrationmobile.domain.Host
import com.imm.operationsorchestrationmobile.repository.HostRepository
import com.imm.operationsorchestrationmobile.util.Database
import com.imm.operationsorchestrationmobile.util.IntentUtils
import com.imm.operationsorchestrationmobile.util.IntentUtils.Companion.navigateToActivityNoHistory
import kotlinx.android.synthetic.main.activity_hosts.*
import kotlinx.android.synthetic.main.add_dialog.view.*
import kotlinx.android.synthetic.main.content_hosts.*

class HostsActivity : AppCompatActivity() {
    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hosts)
        setSupportActionBar(toolbar)

        currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            navigateToActivityNoHistory(this, LoginActivity::class)
        } else {
            initializeActivity()
        }
    }

    fun initializeActivity() {
        val hostRepository = HostRepository();

        val database = Database.getDatabase()
        val myRef = database.getReference(currentUser?.uid);

        val myRecyclerView: RecyclerView = findViewById(R.id.hostsRecyclerView);
        val myRecyclerAdapter = HostListAdapter(hostRepository, myRef, this);
        val myRecyclerManager = LinearLayoutManager(this);

        myRecyclerAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                if (myRecyclerAdapter.getItemCount() == 0) {
                    emptyMessage.visibility = View.VISIBLE
                    hostsRecyclerView.visibility = View.GONE
                } else {
                    emptyMessage.visibility = View.GONE
                    hostsRecyclerView.visibility = View.VISIBLE
                }
            }
        })

        myRecyclerView.layoutManager = myRecyclerManager;
        myRecyclerView.adapter = myRecyclerAdapter;
        myRecyclerView.setHasFixedSize(true);

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.getValue<HostRepository>(HostRepository::class.java) != null) {
                    hostRepository.setHostsList(dataSnapshot
                            .getValue<HostRepository>(HostRepository::class.java)!!
                            .getHostsList())
                }
                myRecyclerAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Database read failed: " + databaseError.code)
            }
        })

        actionButton.setOnClickListener { view ->
            val myBuilder = AlertDialog.Builder(this)
            val myView = layoutInflater.inflate(R.layout.add_dialog, null)

            myBuilder.setView(myView)
            myBuilder.setTitle("Add new host").setCancelable(true).setPositiveButton("Add") { dialog, whichButton ->
                val host = Host(myView.addDisplayName.text.toString(), myView.addHostname.text.toString(), myView.checkboxSecure.isChecked)

                hostRepository.addHost(host)
                myRecyclerAdapter.notifyItemInserted(hostRepository.getHostsList().size)
                myRef.setValue(hostRepository)
            }

            val dialog = myBuilder.create()
            dialog.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()

        if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut()
            if (FirebaseAuth.getInstance().currentUser == null) {
                val myBuilder = AlertDialog.Builder(this)
                val myView = layoutInflater.inflate(R.layout.sign_out_dialog, null)

                myBuilder.setView(myView)
                myBuilder.setTitle("Sign out")
                        .setCancelable(true)
                        .setPositiveButton("Log out") { dialog, whichButton ->
                            IntentUtils.navigateToActivityNoHistory(this, LoginActivity::class)
                        }
                        .setNegativeButton("Cancel") { dialog, whichButton ->
                            dialog.dismiss()
                        }

                val dialog = myBuilder.create()
                dialog.show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

}
