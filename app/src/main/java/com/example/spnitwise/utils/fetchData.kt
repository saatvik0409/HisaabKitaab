package com.example.spnitwise.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale
import java.util.concurrent.CountDownLatch

class fetchData {

    companion object {

        private var TAG = "FETCH_DATA_TAG"
        fun fetchUserProfileData(context: Context){

            val profileDocReference = getDocumentReference(
                REF_SPECIFIC_USER,
                mapOf(
                    NAME_FIELD_USERNAME to ThisUserObject.userName
                )
            )
            //New Code
            profileDocReference.get().addOnCompleteListener{task ->
                if (task.isSuccessful){
                    Log.i(TAG,"USERS/${ThisUserObject.userName} :- Fetched the data successfully")
                    val fetchedDocument = task.result

                    //Assigning and mapping
                    ThisUserObject.overallTotal = (fetchedDocument[NAME_FIELD_TOTAL] as? Double)?.toFloat() ?: 0.0f
                    ThisUserObject.groupsTotal = (fetchedDocument[NAME_FIELD_TOTAL_GROUP] as? Double)?.toFloat() ?: 0.0f
                    ThisUserObject.nonGroupsTotal = (fetchedDocument[NAME_FIELD_TOTAL_NON_GROUP] as? Double)?.toFloat() ?: 0.0f

                    fetchFriendsGroupsAndExpensesCollections()
                    return@addOnCompleteListener
                }else{
                    Log.e(TAG,"USERS/${ThisUserObject.userName} :- Failed to fetch data")
                }

            }

            //Old Code
            //TODO: Remove this shit
//            val docRef: DocumentReference = dbFirestore.collection(USERS_COLLECTION)
//                .document(authFirestore.currentUser?.email.toString())
//                .collection(PROFILE_COLLECTION)
//                .document(PROFILE_DOC)
//
//            docRef.get().addOnCompleteListener{task ->
//                if(task.isSuccessful){
//                    Log.i(TAG, "Fetched the document from firestore successfully")
//                    val document = task.result
//
//                    dataOverallTot = (document[PROFILE_DOC_TOTAL] as? Double)?.toFloat() ?: 0.0f
//                    dataGroupsTot = (document[PROFILE_DOC_TOTAL_G] as? Double)?.toFloat() ?: 0.0f
//                    dataNonGroupsTot = (document[PROFILE_DOC_TOTAL_NG] as? Double)?.toFloat() ?: 0.0f
//
////                    dataGroups = (document[PROFILE_DOC_GROUPS] as? List<String>) ?: emptyList()
////                    dataFriends = (document[PROFILE_DOC_FRIENDS] as? Map<String?, Float?>) ?: emptyMap()
//                    fetchFriendsGroupsAndExpenses()
//                    Log.i(TAG,"Fetching data completed, BYE BYE")
//                    return@addOnCompleteListener
//                }else{
//                    Log.e(TAG, "Fetched the document from firestore unsuccessfully")
//                    Toast.makeText(context,"Fetched the document unsuccessfully", Toast.LENGTH_LONG).show()
//                }
//            }
        }

        fun fetchFriendsGroupsAndExpensesCollections(){
            val friendsListReference = getCollectionReference(
                REF_FRIENDS,
                mapOf(NAME_FIELD_USERNAME to ThisUserObject.userName)
            )

            val groupsListReference = getCollectionReference(
                REF_UGROUPS,
                mapOf(NAME_FIELD_USERNAME to ThisUserObject.userName)
            )

            val expensesListReference = getCollectionReference(
                REF_EXPS,
                mapOf(NAME_FIELD_USERNAME to ThisUserObject.userName)
            )

            val task1 = friendsListReference.get()
            val task2 = groupsListReference.get()
            val task3 = expensesListReference.get()

            Tasks.whenAllComplete(task1,task2,task3).addOnCompleteListener{tasks ->
                if (task1.isSuccessful && task2.isSuccessful && task3.isSuccessful){
                    Log.i(TAG,"USERS/${ThisUserObject.userName}/Expenses,Friends,Groups :- Successfully fetched all the required data")
                    for (document in task1.result){
                        val friendName : String = (document[NAME_FIELD_FRIEND_USERNAME] as? String) ?: ""
                        val friendBalance : Float = (document[NAME_FIELD_BALANCE] as? Double)?.toFloat() ?: 0.0f
                        ThisUserObject.friendsList.add(Pair(friendName,friendBalance))
                        if (ThisUserObject.friendsList.size == task1.result.size()){
                            Log.i(TAG,"USERS/${ThisUserObject.userName}/Friends :- Completed storing data into ThisUserObject.friendsList")
                        }
                    }

                    for (document in task2.result){
                        val groupName : String = (document[NAME_FIELD_GROUP_NAME] as? String) ?: ""
                        val groupID : String = (document[NAME_FIELD_GROUP_ID] as? String) ?: ""
                        val groupBalance : Float = (document[NAME_FIELD_BALANCE] as? Double)?.toFloat() ?: 0.0f
                        ThisUserObject.groupsList.add(Triple(groupName,groupID,groupBalance))
                        if (ThisUserObject.groupsList.size == task2.result.size()){
                            Log.i(TAG,"USERS/${ThisUserObject.userName}/Groups :- Completed storing data into ThisUserObject.groupsList")
                        }
                    }

                    for (document in task3.result){
                        val groupID : String = (document[NAME_FIELD_GROUP_ID] as? String) ?: ""
                        val amount : Float = (document[NAME_FIELD_AMOUNT] as? Float) ?: 0.0f
                        val timeStamp : Long = (document[NAME_FIELD_TIME_STAMP] as? Long) ?: 0
                        val paidByMap : Map<String,Float> = (document[NAME_FIELD_PAID_BY] as? Map<String,Float>)?: emptyMap()
                        val paidToMap : Map<String,Float> = (document[NAME_FIELD_PAID_TO] as? Map<String,Float>)?: emptyMap()
                        val description : String = (document[NAME_FIELD_DESCRIPTION] as? String)?: ""

                        val timeStampInstance = convertUNIXtoTimeStamp(timeStamp)

                        val thisExpenseData = ExpensesData(
                            groupID,
                            timeStampInstance,
                            amount,
                            paidByMap,
                            paidToMap,
                            description
                        )
                        ThisUserObject.expensesList.add(thisExpenseData)
                        if (ThisUserObject.expensesList.size == task3.result.size()){
                            Log.i(TAG,"USERS/${ThisUserObject.userName}/Expenses :- Completed storing expenses data in ThisUserObject.expenseList")
                        }
                    }
                    _profileDataFetched.value = true
                }
                else{
                    if (!task1.isSuccessful){
                        Log.e(TAG,"USERS/${ThisUserObject.userName}/Friends :- Failed to fetch this data")
                    }
                    if (!task2.isSuccessful){
                        Log.e(TAG,"USERS/${ThisUserObject.userName}/Groups :- Failed to fetch this data")
                    }
                    if (!task3.isSuccessful){
                        Log.e(TAG,"USERS/${ThisUserObject.userName}/Expenses :- Failed to fetch this data")
                    }
                }
            }
        }

        //TODO: Remove this old code as well as this is no longer in use
        fun fetchFriendsGroupsAndExpenses(){
            val collectionRefFriends : CollectionReference = dbFirestore.collection(USERS_COLLECTION)
                .document(authFirestore.currentUser?.email.toString())
                .collection(PROFILE_COLLECTION)
                .document(PROFILE_DOC)
                .collection(PROFILE_DOC_FRIENDS_COLLECTION)

            val collectionRefGroups : CollectionReference = dbFirestore.collection(USERS_COLLECTION)
                .document(authFirestore.currentUser?.email.toString())
                .collection(PROFILE_COLLECTION)
                .document(PROFILE_DOC)
                .collection(PROFILE_DOC_GROUPS_COLLECTION)

            val collectionRefExpenses : CollectionReference = dbFirestore.collection(USERS_COLLECTION)
                .document(authFirestore.currentUser?.email.toString())
                .collection(EXPENSES_COLLECTION)

            val task1 = collectionRefFriends.get()
            val task2 = collectionRefGroups.get()
            val task3 = collectionRefExpenses.get()



            //Document structure of expenses
            //Expense Time stamp: Int -> current time millis()
            //Expense Amount (Involvement): Float
            //Expense Group: String -> GroupID -> "" if no group
            //Paid by: Map<String, Float> -> Payers mapped to paid amounts (UserID -> Float)
            //Paid to: Map<String, Float> -> UdhaarBaaj mapped to udhaari (UserID -> Float)
            //Description: Expense ka description
            //Koi bhi negative numbers nahi honge dono maps mein

            //Location of expenses data is -> Users -> user ka email -> expenses -> all expenses go here >_<

            Tasks.whenAllComplete(task1,task2,task3).addOnCompleteListener{tasks ->
                if (task1.isSuccessful && task2.isSuccessful && task3.isSuccessful){
                    for (document in task1.result){
                        val friendName : String = (document["FriendID"] as? String) ?: ""
                        val friendBalance : Float = (document["Balance"] as? Double)?.toFloat() ?: 0.0f
                        dataFriends.add(Pair(friendName,friendBalance))
                        if (dataFriends.size == task1.result.size()){
                            Log.i(TAG,"Completed friends data fetching")
                        }
                    }
                    for (document in task2.result){
                        val groupName : String = (document["GroupName"] as? String) ?: ""
                        val groupID : String = (document["GroupID"] as? String) ?: ""
                        val groupBalance : Float = (document["GroupBalance"] as? Double)?.toFloat() ?: 0.0f
                        dataGroups.add(Triple(groupName,groupID,groupBalance))
                        if (dataGroups.size == task2.result.size()){
                            Log.i(TAG,"Completed groups data fetching")
                        }
                    }
                    for (document in task3.result){
                        val groupID : String = (document["GroupID"] as? String) ?: ""
                        val amount : Float = (document["Amount"] as? Float) ?: 0.0f
                        val timeStamp : Long = (document["TimeStamp"] as? Long) ?: 0
                        val paidByMap : Map<String,Float> = (document["PaidBy"] as? Map<String,Float>)?: emptyMap()
                        val paidToMap : Map<String,Float> = (document["PaidTo"] as? Map<String,Float>)?: emptyMap()
                        val description : String = (document["Description"] as? String)?: ""

                        val timeStampInstance = convertUNIXtoTimeStamp(timeStamp)

                        val thisExpenseData = ExpensesData(
                            groupID,
                            timeStampInstance,
                            amount,
                            paidByMap,
                            paidToMap,
                            description
                        )
                        dataExpenses.add(thisExpenseData)
                        if (dataExpenses.size == task3.result.size()){
                            Log.i(TAG,"Completed fetching expenses data")
                        }
                    }

                    _profileDataFetched.value = true
                }else{
                    Log.e(TAG,"Error fetching Group and Friends data")
                }

            }
        }
//        fun fetchFriends(){
//            val collectionRef : CollectionReference = dbFirestore.collection(USERS_COLLECTION)
//                .document(authFirestore.currentUser?.email.toString())
//                .collection(PROFILE_COLLECTION)
//                .document(PROFILE_DOC)
//                .collection(PROFILE_DOC_FRIENDS_COLLECTION)
//
//            collectionRef.get().addOnCompleteListener{task ->
//                if (task.isSuccessful){
//                    Log.i(TAG,"Succeeded in finding Friends Collection, Systumm")
//                    Log.i(TAG,"Number of documents: ${task.result.size()}")
//                    for (document in task.result){
//                        val friendName : String = (document["FriendID"] as? String) ?: ""
//                        val friendBalance : Float = (document["Balance"] as? Double)?.toFloat() ?: 0.0f
//                        dataFriends.add(Pair(friendName,friendBalance))
//                        if (dataFriends.size == task.result.size()){
//                            Log.i(TAG,"Count downed latch in friends")
//                        }
//                    }
//
//                }else{
//                    Log.i(TAG,"xud gaye guru")
//                }
//            }
//
//        }
//
//        fun fetchGroups(){
//            val collectionRef : CollectionReference = dbFirestore.collection(USERS_COLLECTION)
//                .document(authFirestore.currentUser?.email.toString())
//                .collection(PROFILE_COLLECTION)
//                .document(PROFILE_DOC)
//                .collection(PROFILE_DOC_GROUPS_COLLECTION)
//
//            collectionRef.get().addOnCompleteListener{task ->
//                if (task.isSuccessful){
//                    Log.i(TAG,"Succeeded in finding Groups Collection, Systumm")
//                    Log.i(TAG,"Number of documents: ${task.result.size()}")
//                    for (document in task.result){
//                        val groupName : String = (document["GroupName"] as? String) ?: ""
//                        val groupID : String = (document["GroupID"] as? String) ?: ""
//                        val groupBalance : Float = (document["GroupBalance"] as? Double)?.toFloat() ?: 0.0f
//                        dataGroups.add(Triple(groupName,groupID,groupBalance))
//                        if (dataGroups.size == task.result.size()){
//                            Log.i(TAG,"Count downed latch in groups")
//                        }
//                    }
//
//                }else{
//                    Log.i(TAG,"xud gaye guru")
//                }
//            }
//
//        }
    }



}