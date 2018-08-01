package com.imm.operationsorchestrationmobile.util

import android.content.Context
import android.content.Intent
import kotlin.reflect.KClass

/**
 * Created by marisca on 26/2/2018.
 */
class IntentUtils {
    companion object {
        fun <T : Any> navigateToActivityNoHistory(context: Context, activity: KClass<T>) {
            val intent = Intent(context, activity.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }

        fun <T : Any> navigateToActivity(context: Context, activity: KClass<T>) {
            val intent = Intent(context, activity.java)
            context.startActivity(intent)
        }
    }
}