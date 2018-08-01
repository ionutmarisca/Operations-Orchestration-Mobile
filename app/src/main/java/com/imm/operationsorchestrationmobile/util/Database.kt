package com.imm.operationsorchestrationmobile.util

import com.google.firebase.database.FirebaseDatabase


/**
 * Created by ionutmarisca on 03/05/2018.
 */
object Database {
    private var database: FirebaseDatabase? = null

    fun getDatabase(): FirebaseDatabase {
        if (database == null) {
            database = FirebaseDatabase.getInstance()
            database!!.setPersistenceEnabled(true)
        }
        return database as FirebaseDatabase
    }
}