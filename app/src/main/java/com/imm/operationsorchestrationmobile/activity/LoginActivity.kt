package com.imm.operationsorchestrationmobile.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.imm.operationsorchestrationmobile.R
import com.imm.operationsorchestrationmobile.util.IntentUtils
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private var loginTag: String = "LoginActivity"
    private var mAuth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()


        loginButton.setOnClickListener { view ->
            signIn(input_email.text.toString(), input_password.text.toString())
        }

        guestButton.setOnClickListener { view ->
            signInAsGuest()
        }

        signupLink.setOnClickListener { view ->
            IntentUtils.navigateToActivity(this, RegisterActivity::class)
        }
    }

    public override fun onStart() {
        super.onStart()
        currentUser = mAuth?.getCurrentUser()
    }


    private fun signIn(email: String, password: String) {
        try {
            mAuth?.signInWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d(loginTag, "signInWithEmail:success")
                            currentUser = mAuth?.currentUser
                            IntentUtils.navigateToActivityNoHistory(this, HostsActivity::class)
                        } else {
                            showFailureToast(task.exception)
                        }
                    }
        } catch (e: IllegalArgumentException) {
            showFailureToast(e)
        }
    }

    private fun signInAsGuest() {
        try {
            mAuth?.signInAnonymously()
                    ?.addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d(loginTag, "signInAnonymously:success")
                            currentUser = mAuth?.currentUser
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
        Log.w(loginTag, "signInWithEmail:failure", e)
        Toast.makeText(this, "Authentication failed.",
                Toast.LENGTH_SHORT).show()
    }
}
