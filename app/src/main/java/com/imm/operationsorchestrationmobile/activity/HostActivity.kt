package com.imm.operationsorchestrationmobile.activity

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.imm.operationsorchestrationmobile.R
import com.imm.operationsorchestrationmobile.fragment.FlowExecutionFragment
import com.imm.operationsorchestrationmobile.fragment.HostDashboardFragment
import com.imm.operationsorchestrationmobile.fragment.RunExplorerFragment
import com.imm.operationsorchestrationmobile.service.OOApiService.Companion.getTotalRoi
import com.imm.operationsorchestrationmobile.util.NukeSSL
import kotlinx.android.synthetic.main.activity_host.*
import kotlinx.android.synthetic.main.app_bar_host.*
import kotlinx.android.synthetic.main.nav_header_host.view.*


class HostActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var displayName: String = ""
    var hostname: String = ""
    var username: String = ""
    var password: String = ""
    val manager = supportFragmentManager
    var isChecked = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)

        val nukeSSL: NukeSSL = NukeSSL();
        nukeSSL.nuke()

        setSupportActionBar(toolbar)

        val intent = intent
        displayName = intent.getStringExtra("displayName")
        hostname = intent.getStringExtra("hostname")
        username = intent.getStringExtra("username")
        password = intent.getStringExtra("password")

        getTotalRoi(applicationContext, applicationContext, fab, hostname, username, password)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.getHeaderView(0).displayNameDrawer.setText(displayName)
        nav_view.getHeaderView(0).hostnameDrawer.setText(hostname)

        nav_view.setNavigationItemSelectedListener(this)
        isChecked = 0
        nav_view.menu.getItem(0).isChecked = true;

        showDefaultFragment()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.host, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_dashboard -> {
                if (isChecked != 0) {
                    nav_view.menu.getItem(0).isChecked = true;
                    nav_view.menu.getItem(isChecked).isChecked = false;
                    isChecked = 0
                    showDefaultFragment()
                }
            }
            R.id.nav_run_explorer -> {
                if (isChecked != 1) {
                    nav_view.menu.getItem(1).isChecked = true;
                    nav_view.menu.getItem(isChecked).isChecked = false;
                    isChecked = 1
                    showRunExplorerFragment()
                }
            }
            R.id.nav_flow_execution -> {
                if (isChecked != 2) {
                    nav_view.menu.getItem(2).isChecked = true;
                    nav_view.menu.getItem(isChecked).isChecked = false;
                    isChecked = 2
                    showFlowExecutionFragment()
                }
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun showDefaultFragment() {
        val transaction = manager.beginTransaction()
        val fragment = HostDashboardFragment.newInstance(hostname, username, password)
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.commit()
    }

    fun showFlowExecutionFragment() {
        val transaction = manager.beginTransaction()
        val fragment = FlowExecutionFragment.newInstance(hostname, username, password)
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.commit()
    }

    fun showRunExplorerFragment() {
        val transaction = manager.beginTransaction()
        val fragment = RunExplorerFragment.newInstance(hostname, username, password)
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.commit()
    }
}
