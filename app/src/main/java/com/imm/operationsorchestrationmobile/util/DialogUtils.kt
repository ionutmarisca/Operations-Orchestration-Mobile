package com.imm.operationsorchestrationmobile.util

import android.content.Context
import android.content.DialogInterface
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AlertDialog
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import com.imm.operationsorchestrationmobile.R
import com.imm.operationsorchestrationmobile.domain.FlowInput
import com.imm.operationsorchestrationmobile.service.OOApiService
import kotlinx.android.synthetic.main.custom_dialog.view.*

class DialogUtils {
    companion object {
        val editTextMap: MutableMap<String, EditText> = HashMap<String, EditText>()

        fun createAndShowCustomDialog(context: Context, inputsList: ArrayList<FlowInput>, flowUuid: String, hostname: String, username: String, password: String) {
            val myBuilder = AlertDialog.Builder(context)
            val myView = LayoutInflater.from(context).inflate(R.layout.custom_dialog, null)

            val parentLinearLayout = myView.topLayoutParent

            for (i in inputsList) {
                val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
                val r = context.getResources()
                val px = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        20f,
                        r.getDisplayMetrics()
                ).toInt()
                params.setMargins(px, px, px, 0)

                val textInputLayout = TextInputLayout(ContextThemeWrapper(context, R.style.TextLabel_Dark))
                textInputLayout.layoutParams = params

                val editTextParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )

                val editText = EditText(context)
                editText.layoutParams = editTextParams
                editText.hint = i.displayName
                if (i.required)
                    editText.hint = editText.hint.toString() + "*"

                textInputLayout.addView(editText)
                editTextMap[i.displayName] = editText

                parentLinearLayout.addView(textInputLayout)
            }

            myBuilder.setView(myView)
            myBuilder.setOnCancelListener(DialogInterface.OnCancelListener {
                //Handle OnCancelListener in the future
            })
            myBuilder.setTitle("Execute flow").setCancelable(true).setPositiveButton("Execute") { dialog, whichButton ->
                OOApiService.sendExecutionRequest(context, hostname, username, password, flowUuid)
            }

            val dialog = myBuilder.create()
            dialog.show()
        }
    }
}