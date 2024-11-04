package com.example.spnitwise.utils

import android.icu.number.IntegerWidth
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

public var DISABLE_BOTTOM_MENU_LISTENER = false

public var _profileDataFetched = MutableLiveData<Boolean>(false)
public var _allUsersDataFetched = MutableLiveData<Boolean>(false)

public var dbFirestore = Firebase.firestore
public var authFirestore = Firebase.auth
public var thisUser : String = ""

public var dataOverallTot : Float = 0.0f
public var dataGroupsTot : Float =  0.0f
public var dataNonGroupsTot : Float = 0.0f

public var dataFriends : MutableList<Pair<String,Float>> = mutableListOf()//Friend ID -> Mapped to overall expense
public var dataGroups : MutableList<Triple<String,String,Float>> = mutableListOf()//Group ID, Group Name, Overall status

public var allSpnitwiseUsers : List<String> = emptyList()
public var dataExpenses: MutableList<ExpensesData> = mutableListOf()


public fun convertUNIXtoTimeStamp(unixTime : Long): TimeStamp{
    val date = Date(unixTime)

    val dayFormatter = SimpleDateFormat("dd", Locale.getDefault())        // Day as two digits
    val monthFormatter = SimpleDateFormat("MMM", Locale.getDefault())     // Three-letter month abbreviation
    val hourFormatter = SimpleDateFormat("HH", Locale.getDefault())       // Hour in 24-hour format
    val minutesFormatter = SimpleDateFormat("mm", Locale.getDefault())    // Minutes as two digits
    val yearFormatter = SimpleDateFormat("yyyy", Locale.getDefault())

    val year = yearFormatter.format(date).toInt()
    val month = monthFormatter.format(date)
    val day = dayFormatter.format(date)
    val hour = hourFormatter.format(date)
    val minutes = minutesFormatter.format(date)

    val timeStampInstance = TimeStamp(
        year = year,
        month = month,
        day = day,
        hour = hour,
        minutes = minutes
    )
    return timeStampInstance
}
public fun getCollectionReference(type: Int, args : Map<String,String>) : CollectionReference{

    var reference : CollectionReference = dbFirestore.collection("Default") //Default value

    when (type) {

        REF_USER -> {
            reference = dbFirestore.collection(NAME_COLLECTION_USERS)
        }

        REF_EXPS -> {
            reference = dbFirestore
                .collection(NAME_COLLECTION_USERS)
                .document(args[NAME_FIELD_USERNAME]?:"")
                .collection(NAME_COLLECTION_EXPENSES)
        }

        REF_FRIENDS -> {
            reference = dbFirestore
                .collection(NAME_COLLECTION_USERS)
                .document(args[NAME_FIELD_USERNAME]?:"")
                .collection(NAME_COLLECTION_FRIENDS)
        }

        REF_UGROUPS -> {
            reference = dbFirestore
                .collection(NAME_COLLECTION_USERS)
                .document(args[NAME_FIELD_USERNAME]?:"")
                .collection(NAME_COLLECTION_UGROUPS)
        }

        REF_ALLUSER -> {
            reference = dbFirestore
                .collection(NAME_COLLECTION_ALL_USERS)
        }
    }

    return reference
}
public fun getDocumentReference(type: Int, args : Map<String,String>) : DocumentReference {
    var reference : DocumentReference = dbFirestore.collection("Default").document("Default")

    when (type){

        REF_SPECIFIC_USER -> {
            reference = dbFirestore
                .collection(NAME_COLLECTION_USERS)
                .document(args[NAME_FIELD_USERNAME]?:"")
        }

        REF_SPECIFIC_FRIENDS -> {
            reference = dbFirestore
                .collection(NAME_COLLECTION_USERS)
                .document(args[NAME_FIELD_USERNAME]?:"")
                .collection(NAME_COLLECTION_FRIENDS)
                .document(args[NAME_FIELD_FRIEND_USERNAME]?:"")
        }

        REF_SPECIFIC_EXPS -> {
            reference = dbFirestore
                .collection(NAME_COLLECTION_USERS)
                .document(args[NAME_FIELD_USERNAME]?:"")
                .collection(NAME_COLLECTION_EXPENSES)
                .document(args[NAME_FIELD_EXPENSE_ID]?:"")
        }

        REF_SPECIFIC_UGROUPS -> {
            reference = dbFirestore
                .collection(NAME_COLLECTION_USERS)
                .document(args[NAME_FIELD_USERNAME]?:"")
                .collection(NAME_COLLECTION_UGROUPS)
                .document(args[NAME_FIELD_GROUP_ID]?:"")
        }

        REF_SPECIFIC_ALLUSER -> {
            reference = dbFirestore
                .collection(NAME_COLLECTION_ALL_USERS)
                .document(args[NAME_FIELD_USERNAME]?:"")
        }
    }

    return reference
}
public fun updateProfileData(user: String, updates : MutableMap<String,Float>){
    val TAG = "UPDATE_PROFILE_DATA"

    val docReg : DocumentReference = dbFirestore
        .collection(USERS_COLLECTION)
        .document(user)
        .collection(PROFILE_COLLECTION)
        .document(PROFILE_DOC)


    docReg.update(updates as Map<String, Any>).addOnSuccessListener {
        Log.i(TAG, "Updated the data successfully")
    }.addOnFailureListener{
        Log.i(TAG, "Failed to update data, system hang")
    }
}

