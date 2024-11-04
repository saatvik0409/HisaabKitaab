package com.example.spnitwise.utils

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.spnitwise.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class SignupActivity: AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btSignUp: Button
    private lateinit var auth: FirebaseAuth

    private var db = Firebase.firestore

    companion object {
        private var TAG = "SignUpActivityTag"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        //Initializing variables
        etUsername = findViewById(R.id.et_ls_username)
        etPassword = findViewById(R.id.et_ls_password)
        btSignUp = findViewById(R.id.bt_ls_signup)
        auth = Firebase.auth

        //AddingListeners
        etUsername.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                btSignUp.isEnabled = canEnableBtSignUp()
            }
        })

        etPassword.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                btSignUp.isEnabled = canEnableBtSignUp()
            }
        })

        btSignUp.setOnClickListener{
            trySignUp(etUsername.text.toString(),etPassword.text.toString())
        }
    }
//
    private fun trySignUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser

                    //Creates the new user profile on firestore
                    createNewUserOnFirestore(email, password)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        this,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun createNewUserOnFirestore(email: String, password: String) {

        val newUserProfileReference = getDocumentReference(
            REF_SPECIFIC_USER,
            mapOf(
                NAME_FIELD_USERNAME to email
            )
        )

        newUserProfileReference
            .set(
                mapOf(
                    NAME_FIELD_USERNAME to email,
                    NAME_FIELD_TOTAL to 0,
                    NAME_FIELD_TOTAL_GROUP to 0,
                    NAME_FIELD_TOTAL_NON_GROUP to 0
                )
            ).addOnCompleteListener{task ->
                if (task.isSuccessful) Log.i(TAG,"USERS/${email} :- Successfully created signup document")
                else Log.e(TAG,"USERS/${email} :- Failed to create the signup document")
            }

        val newUserAllUserDocReference = getDocumentReference(
            REF_SPECIFIC_ALLUSER,
            mapOf(
                NAME_FIELD_USERNAME to email
            )
        )

        newUserAllUserDocReference
            .set(
                mapOf(
                    NAME_FIELD_USERNAME to email
                )
            )
            .addOnCompleteListener{task ->
                if (task.isSuccessful) {
                    Log.i(TAG, "ALLUSERS/${email} :- Successfully appended document to all document list")
                    finish()
                }
                else Log.e(TAG, "ALLUSERS/${email} :- Failed to append the document to the list")
            }

//        db.collection(USERS_COLLECTION).document(email).collection(PROFILE_COLLECTION).document(PROFILE_DOC)
//            .set(mapOf(
//                "Username" to email,
//                "Total" to 0,
//                "Total_G" to 0,
//                "Total_NG" to 0
//            ))
//            .addOnCompleteListener { profileCreationTask ->
//                if (!profileCreationTask.isSuccessful){
//                    Log.e(TAG,"Failed to create firestore document")
//                    Toast.makeText(this,"Failed to create fire store authentication",Toast.LENGTH_SHORT).show()
//                    return@addOnCompleteListener
//                }
//                Log.i(TAG,"Successfully Created firestore document")
//                Toast.makeText(this,"Successfully created firestore document",Toast.LENGTH_SHORT).show()
//            }
//
//        db.collection(ALL_USERS_COLLECTON).document(email)
//            .set(mapOf(
//                "userName" to email
//            ))
//            .addOnCompleteListener{allUserDocumentCreationTask ->
//                if (!allUserDocumentCreationTask.isSuccessful){
//                    Log.e(TAG,"Failed to create username document")
//                    return@addOnCompleteListener
//                }
//                Log.i(TAG,"Successfully created allUsersDocument")
//                finish()
//            }

//        db.collection(USERS_COLLECTION).document(email).collection(PROFILE_COLLECTION).document(PROFILE_DOC)
//            .collection(PROFILE_DOC_FRIENDS_COLLECTION).document("mangalsaatvik@gmail.com")
//            .set(
//                mapOf(
//                    "FriendID" to "mangalsaatvik@gmail.com",
//                    "Balance" to 30.0f
//                ))
//            .addOnCompleteListener{newFriendTask ->
//                if (newFriendTask.isSuccessful){
//                    Log.i(TAG,"NayeDost banane mein hum safal rahe, thanku")
//                }else{
//                    Log.i(TAG, "Mai dosti ke kaabil hi nahi hu :D")
//                }
//            }
//
//        db.collection(USERS_COLLECTION).document(email).collection(PROFILE_COLLECTION).document(PROFILE_DOC)
//            .collection(PROFILE_DOC_GROUPS_COLLECTION).document("1412455")
//            .set(
//                mapOf(
//                    "GroupName" to "KhanMarketGanga",
//                    "GroupID" to "1412455",
//                    "GroupBalance" to 30.0f
//                ))
//            .addOnCompleteListener{newFriendTask ->
//                if (newFriendTask.isSuccessful){
//                    Log.i(TAG,"Naya group ban gaya, for the Nth time")
//                }else{
//                    Log.i(TAG, "Iss baar toh naya group bhi nahi mila :D")
//                }
//            }
    }

    private fun canEnableBtSignUp(): Boolean {
        return etUsername.text.toString().length > 4 && etPassword.text.toString().length > 7
    }

}
