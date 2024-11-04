package com.example.spnitwise

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.spnitwise.utils.SignupActivity
import com.example.spnitwise.utils.ThisUserObject
import com.example.spnitwise.utils._profileDataFetched
import com.example.spnitwise.utils.convertUNIXtoTimeStamp
import com.example.spnitwise.utils.fetchData
import com.example.spnitwise.utils.homeActivity
import com.example.spnitwise.utils.thisUser
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var etLoginUser: EditText
    private lateinit var etLoginPass: EditText
    private lateinit var btLogin: Button
    private lateinit var btnSignup: Button

    companion object {
        private var TAG = "MainActivity"
        private var RQ_MAIN_SIGNUP = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //set Content view sets the initial layout which would be displayed on screen!
        setContentView(R.layout.layout_login)

        //Initializing variables
        auth = Firebase.auth
        etLoginUser = findViewById(R.id.et_login_user)
        etLoginPass = findViewById(R.id.et_user_password)
        btLogin = findViewById(R.id.bt_login)
        btnSignup = findViewById(R.id.bt_signUp)

        //Setting Initial values
        btLogin.isEnabled = false
        btnSignup.isEnabled =true


        //Listeners
        etLoginUser.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                btLogin.isEnabled = canEnableBtLogin()
            }
        })

        etLoginPass.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                btLogin.isEnabled = canEnableBtLogin()
            }
        })

        btLogin.setOnClickListener{
            loginAttempt(etLoginUser.text.toString(),etLoginPass.text.toString())
        }

        btnSignup.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }


    }


    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            //TODO: User should be navigated to home page
            Toast.makeText(this,"Welcome back, ${FirebaseAuth.getInstance().currentUser?.email.toString()}!",Toast.LENGTH_SHORT).show()
            enterUserIntoApp()

        }
    }

    private fun enterUserIntoApp(){
        val intent = Intent(this,homeActivity::class.java)
        thisUser = auth.currentUser?.email.toString()
        ThisUserObject.userName = thisUser
        fetchData.fetchUserProfileData(this)
        _profileDataFetched.observe(this) { isFetched ->
            if (isFetched) {
                startActivity(intent)
            }
        }
    }

    private fun loginAttempt(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    Toast.makeText(this,"Successfully signed in as $email",Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    enterUserIntoApp()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        this,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun canEnableBtLogin(): Boolean{
        return etLoginUser.text.toString().length > 4 && etLoginPass.text.toString().length > 7
    }
}

