package com.imm.operationsorchestrationmobile.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.imm.operationsorchestrationmobile.R
import com.imm.operationsorchestrationmobile.util.IntentUtils
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private var loginTag: String = "RegisterActivity"
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        registerButton.setOnClickListener { view ->
            signUp(input_email_register.text.toString(), input_password_register.text.toString())
        }
    }


    private fun signUp(email: String, password: String) {
        try {
            mAuth?.createUserWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d(loginTag, "signInWithEmail:success")
                            Toast.makeText(this, "Register successful. You are now logged in",
                                    Toast.LENGTH_SHORT).show()
                            IntentUtils.navigateToActivityNoHistory(this, HostsActivity::class)
                        } else {
                            showFailureToast(task.exception)
                        }
                    }
        } catch (e: IllegalArgumentException) {
            showFailureToast(e)
        }
    }

    private fun showFailureToast(e: java.lang.Exception?) {
        Log.w(loginTag, "signUp:failure", e)
        Toast.makeText(this, "Register failed",
                Toast.LENGTH_SHORT).show()
    }
}
