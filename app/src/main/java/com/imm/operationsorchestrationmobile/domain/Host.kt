package com.imm.operationsorchestrationmobile.domain

/**
 * Created by ionutmarisca on 01/05/2018.
 */
data class Host(
        var displayName: String,
        var hostname: String,
        var isSecure: Boolean) {
    var id: Int = -1;

    constructor() : this("", "", true)
}