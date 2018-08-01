package com.imm.operationsorchestrationmobile.repository

import com.imm.operationsorchestrationmobile.domain.Host

/**
 * Created by ionutmarisca on 01/05/2018.
 */
class HostRepository {
    private var hostsList: MutableList<Host> = ArrayList<Host>()

    fun addHost(host: Host) {
        host.id = getNextId()
        hostsList.add(host)
    }

    fun deleteHost(id: Int) {
        hostsList.removeAt(id)
        //Repair IDs
        for (i in 0 until hostsList.size) {
            hostsList.get(i).id = i + 1
        }
    }

    fun updateHost(index: Int, host: Host) {
        hostsList.get(index).displayName = host.displayName
        hostsList.get(index).hostname = host.hostname
        hostsList.get(index).isSecure = host.isSecure
    }

    fun getHost(index: Int): Host {
        return hostsList.get(index)
    }

    fun getHostsList(): List<Host> {
        return this.hostsList
    }

    fun setHostsList(hostsList: List<Host>) {
        this.hostsList = hostsList.toMutableList()
    }

    private fun getNextId(): Int {
        return if (hostsList?.size === 0) {
            1
        } else {
            hostsList.size
        }
    }
}