package com.example.spnitwise

import android.app.appsearch.AppSearchSchema.DocumentPropertyConfig
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spnitwise.adapters.ExpensesCreateAdapter
import com.example.spnitwise.utils.EXPENSES_COLLECTION
import com.example.spnitwise.utils.ExpenseCardModel
import com.example.spnitwise.utils.ExpensesData
import com.example.spnitwise.utils.NAME_FIELD_AMOUNT
import com.example.spnitwise.utils.NAME_FIELD_DESCRIPTION
import com.example.spnitwise.utils.NAME_FIELD_GROUP_ID
import com.example.spnitwise.utils.NAME_FIELD_PAID_BY
import com.example.spnitwise.utils.NAME_FIELD_PAID_TO
import com.example.spnitwise.utils.NAME_FIELD_TIME_STAMP
import com.example.spnitwise.utils.PROFILE_COLLECTION
import com.example.spnitwise.utils.PROFILE_DOC
import com.example.spnitwise.utils.PROFILE_DOC_FRIENDS_COLLECTION
import com.example.spnitwise.utils.PROFILE_DOC_TOTAL
import com.example.spnitwise.utils.PROFILE_DOC_TOTAL_NG
import com.example.spnitwise.utils.USERS_COLLECTION
import com.example.spnitwise.utils.convertUNIXtoTimeStamp
import com.example.spnitwise.utils.dataExpenses
import com.example.spnitwise.utils.dataFriends
import com.example.spnitwise.utils.dataNonGroupsTot
import com.example.spnitwise.utils.dataOverallTot
import com.example.spnitwise.utils.dbFirestore
import com.example.spnitwise.utils.thisUser
import com.example.spnitwise.utils.updateProfileData
import com.google.firebase.firestore.DocumentReference
import kotlin.math.round
import kotlin.math.roundToInt

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "friendID"
private const val ARG_PARAM2 = "friendIndex"

/**
 * A simple [Fragment] subclass.
 * Use the [AddFriendExpenseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddFriendExpenseFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var friendID: String = ""
    private var friendIndex : Int = 0

    private lateinit var tvWithYouString : TextView
    private lateinit var tvSplitRemaining : TextView
    private lateinit var tvPaidRemaining : TextView
    private lateinit var btAddExpense : Button
    private lateinit var etExpDesc : EditText
    private lateinit var etExpAmount : EditText
    private lateinit var spPaidBy : Spinner
    private lateinit var spSplit : Spinner
    private lateinit var rvPaidBy : RecyclerView
    private lateinit var rvSplit : RecyclerView
    private lateinit var adapterSplit : ExpensesCreateAdapter
    private lateinit var adapterPaidBy : ExpensesCreateAdapter
    companion object {
        private val TAG = "ADD_FRIEND_EXPENSE_FRAGMENT_TAG"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            friendID = it.getString(ARG_PARAM1)?: ""
            friendIndex = it.getInt(ARG_PARAM2)?: 0
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_friend_expense, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvWithYouString = view.findViewById(R.id.tv_fafe_expenseWith)
        tvSplitRemaining = view.findViewById(R.id.tv_fafe_splitRemainingAmount)
        tvPaidRemaining = view.findViewById(R.id.tv_fafe_paidByRemainingAmount)
        btAddExpense = view.findViewById(R.id.bt_fafe_completed)
        etExpDesc = view.findViewById(R.id.et_fafe_expenseDesc)
        etExpAmount = view.findViewById(R.id.et_fafe_amount)
        spPaidBy = view.findViewById(R.id.sp_fafe_paidBy)
        spSplit = view.findViewById(R.id.sp_fafe_split)
        rvPaidBy = view.findViewById(R.id.rv_fafe_paidBy)
        rvSplit = view.findViewById(R.id.rv_fafe_split)

        val paidByOptions = mutableListOf<String>("You",friendID,"Both")
//        for ((index, value) in paidByOptions.withIndex()) {
//            if (value.length > 7){
//                paidByOptions[index] = value.substring(0,6)+'.'
//            }
//        }

        val splitOptions = mutableListOf<String>("Equally","By amount","By percentages","By Stock")

        val adapterSpPaid = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, paidByOptions)
        adapterSpPaid.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spPaidBy.adapter = adapterSpPaid
        spPaidBy.setSelection(0)

        val adapterSpSplit = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item, splitOptions)
        adapterSpSplit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spSplit.adapter = adapterSpSplit
        spSplit.setSelection(0)

        val paidByUserList = mutableListOf<String>(thisUser,friendID)
        val paidByExpenseList = mutableListOf<ExpenseCardModel>(ExpenseCardModel(),ExpenseCardModel())
        adapterPaidBy = ExpensesCreateAdapter(
            paidByUserList,
            paidByExpenseList,
            0.0f,
            "amount"){total ->
            updateTotal(tvPaidRemaining,total)
        }
        rvPaidBy.adapter = adapterPaidBy
        rvPaidBy.layoutManager = LinearLayoutManager(requireContext())

        val splitUserList = paidByUserList
        val splitExpenseList = mutableListOf<ExpenseCardModel>(
            ExpenseCardModel(),
            ExpenseCardModel()
        )

        adapterSplit = ExpensesCreateAdapter(
            splitUserList,
            splitExpenseList,
            0.0f,
            "amount") { total ->
            updateTotal(tvSplitRemaining,total)
        }

        rvSplit.adapter = adapterSplit
        rvSplit.layoutManager = LinearLayoutManager(requireContext())

        etExpAmount.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                adapterPaidBy.currentRemTotal -= runCatching { s.toString().toFloat() }.getOrDefault(0.0f)
                updateTotal(tvPaidRemaining,adapterPaidBy.currentRemTotal)
                if (adapterSplit.type == "amount"){
                    adapterSplit.currentRemTotal -= runCatching { s.toString().toFloat() }.getOrDefault(0.0f)
                    updateTotal(tvSplitRemaining,adapterSplit.currentRemTotal)
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //TODO("Not yet implemented")

            }

            override fun afterTextChanged(s: Editable?) {
                adapterPaidBy.currentRemTotal += runCatching { s.toString().toFloat() }.getOrDefault(0.0f)
                updateTotal(tvPaidRemaining,adapterPaidBy.currentRemTotal)
                if (adapterSplit.type == "amount") {
                    adapterSplit.currentRemTotal += runCatching { s.toString().toFloat() }.getOrDefault(0.0f)
                    updateTotal(tvSplitRemaining, adapterSplit.currentRemTotal)
                }
            }

        })

        spPaidBy.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Get the selected item
                val selectedItem = parent.getItemAtPosition(position).toString()
                if (selectedItem == "Both"){
                    rvPaidBy.visibility = View.VISIBLE
                    tvPaidRemaining.visibility = View.VISIBLE
                }else{
                    rvPaidBy.visibility = View.GONE
                    tvPaidRemaining.visibility = View.GONE
                }
                Toast.makeText(requireContext(), "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        spSplit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                if (selectedItem == "Equally"){
                    rvSplit.visibility = View.GONE
                    tvSplitRemaining.visibility = View.GONE
                }else{
                    rvSplit.visibility = View.VISIBLE
                    when (selectedItem){
                        "By amount" -> {
                            tvSplitRemaining.visibility = View.VISIBLE
                            adapterSplit.type = "amount"
                            adapterSplit.currentRemTotal = runCatching { etExpAmount.text.toString().toFloat() }.getOrDefault(0.0f)
                            for (instance in adapterSplit.currentEntries){
                                adapterSplit.currentRemTotal -= runCatching { instance.text.toFloat() }.getOrDefault(0.0f)
                            }
                            updateTotal(tvSplitRemaining, adapterSplit.currentRemTotal)
                        }

                        "By percentages" -> {
                            tvSplitRemaining.visibility = View.VISIBLE
                            adapterSplit.type = "percentage"
                            adapterSplit.currentRemTotal = 100.0f
                            for (instance in adapterSplit.currentEntries){
                                adapterSplit.currentRemTotal -= runCatching { instance.text.toFloat()}.getOrDefault(0.0f)
                            }
                            updateTotal(tvSplitRemaining,adapterSplit.currentRemTotal)
                        }

                        "By Stock" ->{
                            tvSplitRemaining.visibility = View.GONE
                            adapterSplit.type = "stock"
                        }
                    }

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        btAddExpense.setOnClickListener{
            if (checkIfAllGood()){
                executeAddExpense()
            }

        }
    }

    private fun executeAddExpense() {
        val expAmount = roundToTwoDecimalPlaces(etExpAmount.text.toString().toFloat())
        val expDesc = etExpDesc.text.toString()
        val timeStamp : Long = System.currentTimeMillis()
        val groupID = ""
        var paidByMap : MutableMap<String,Float> = mutableMapOf()
        var splitMap : MutableMap<String,Float> = mutableMapOf()

        when (spPaidBy.selectedItem){

            "You" -> {
                paidByMap[thisUser] = expAmount
            }

            friendID -> {
                paidByMap[friendID] = expAmount
            }

            "Both" -> {
                for ((index,instance) in adapterPaidBy.currentEntries.withIndex()){
                    val amt = runCatching { roundToTwoDecimalPlaces(instance.text.toFloat()) }.getOrDefault(0.0f)
                    if (amt != 0.0f){
                        paidByMap[adapterPaidBy.usersList[index]] = amt
                    }
                }
            }
        }

        when (spSplit.selectedItem){

            "Equally" -> {
                val firstAmount = roundToTwoDecimalPlaces(expAmount/2)
                splitMap[thisUser] = firstAmount
                splitMap[friendID] = roundToTwoDecimalPlaces(expAmount - firstAmount)
            }

            "By amount" -> {
                for ((index,instance) in adapterSplit.currentEntries.withIndex()){
                    val amt = runCatching { roundToTwoDecimalPlaces(instance.text.toFloat()) }.getOrDefault(0.0f)
                    if (amt != 0.0f){
                        splitMap[adapterPaidBy.usersList[index]] = amt
                    }
                }
            }

            "By percentages" -> {
                var total = 0.0f
                for (i in 1..adapterSplit.currentEntries.size-1){
                    val instance = adapterSplit.currentEntries[i]
                    var amt = runCatching { roundToTwoDecimalPlaces(instance.text.toFloat()) }.getOrDefault(0.0f)
                    if (amt != 0.0f){
                        amt = roundToTwoDecimalPlaces(amt*expAmount/100)
                        if (amt != 0.0f){
                            total += amt
                            splitMap[adapterSplit.usersList[i]] = amt
                        }
                    }
                }
                val remAmt : Float = roundToTwoDecimalPlaces(expAmount - total)
                if (remAmt > 0.0f){
                    splitMap[adapterSplit.usersList[0]] = remAmt
                }
            }

            "By Stock" -> {
                var totalStock = 0.0f

                for (instance in adapterSplit.currentEntries){
                    totalStock += runCatching { instance.text.toFloat() }.getOrDefault(0.0f)
                }

                totalStock = roundToTwoDecimalPlaces(totalStock)
                var total = 0.0f
                for (i in 1..adapterSplit.currentEntries.size-1){
                    val instance = adapterSplit.currentEntries[i]
                    var amt = runCatching { roundToTwoDecimalPlaces(instance.text.toFloat()) }.getOrDefault(0.0f)
                    if (amt != 0.0f){
                        amt = roundToTwoDecimalPlaces(amt*expAmount/totalStock)
                        if (amt != 0.0f){
                            total += amt
                            splitMap[adapterSplit.usersList[i]] = amt
                        }
                    }
                }
                val remAmt : Float = roundToTwoDecimalPlaces(expAmount - total)
                if (remAmt > 0.0f){
                    splitMap[adapterSplit.usersList[0]] = remAmt
                }
            }
        }

        val docRefExpenseThisUser : DocumentReference = dbFirestore.collection(USERS_COLLECTION)
            .document(thisUser)
            .collection(EXPENSES_COLLECTION)
            .document(timeStamp.toString())

        val docRefExpenseFriend : DocumentReference = dbFirestore.collection(USERS_COLLECTION)
            .document(friendID)
            .collection(EXPENSES_COLLECTION)
            .document(timeStamp.toString())


        val instanceTimeStamp = convertUNIXtoTimeStamp(timeStamp)
        dataExpenses.add(
            ExpensesData(
                groupID,
                instanceTimeStamp,
                expAmount,
                paidByMap,
                splitMap,
                expDesc
            )
        )

        docRefExpenseThisUser.set(
            mapOf(
                NAME_FIELD_GROUP_ID to groupID,
                NAME_FIELD_AMOUNT to expAmount,
                NAME_FIELD_TIME_STAMP to timeStamp,
                NAME_FIELD_PAID_BY to paidByMap,
                NAME_FIELD_PAID_TO to splitMap,
                NAME_FIELD_DESCRIPTION to expDesc
            )
        ).addOnSuccessListener {
            Log.i(TAG, "Added expense history successfully for this user")
        }.addOnFailureListener{
            Log.e(TAG, "Failed to add expense history for this user")
        }

        docRefExpenseFriend.set(
            mapOf(
                NAME_FIELD_GROUP_ID to groupID,
                NAME_FIELD_AMOUNT to expAmount,
                NAME_FIELD_TIME_STAMP to timeStamp,
                NAME_FIELD_PAID_BY to paidByMap,
                NAME_FIELD_PAID_TO to splitMap,
                NAME_FIELD_DESCRIPTION to expDesc
            )
        ).addOnSuccessListener {
            Log.i(TAG, "Added expense history successfully for this user")
        }.addOnFailureListener{
            Log.e(TAG, "Failed to add expense history for this user")
        }

        // Calculating how much on nett, did this user give
        var netGive = 0.0f
        netGive = roundToTwoDecimalPlaces(netGive + (paidByMap[thisUser]?:0.0f))
        netGive = roundToTwoDecimalPlaces(netGive - (splitMap[thisUser]?:0.0f))

        //Updating local elements
        dataOverallTot = roundToTwoDecimalPlaces(netGive+ dataOverallTot)
        dataNonGroupsTot = roundToTwoDecimalPlaces(netGive + dataNonGroupsTot)
        dataFriends[friendIndex] = Pair(dataFriends[friendIndex].first, roundToTwoDecimalPlaces(
            dataFriends[friendIndex].second + netGive))

        //Updating Global Elements -> Profiles
        val updatesUserProfile = mutableMapOf(
            PROFILE_DOC_TOTAL to dataOverallTot,
            PROFILE_DOC_TOTAL_NG to dataNonGroupsTot
        )

        val userProfileRef : DocumentReference = dbFirestore
            .collection(USERS_COLLECTION)
            .document(thisUser)
            .collection(PROFILE_COLLECTION)
            .document(PROFILE_DOC)

        val friendProfileRef : DocumentReference = dbFirestore
            .collection(USERS_COLLECTION)
            .document(friendID)
            .collection(PROFILE_COLLECTION)
            .document(PROFILE_DOC)

        friendProfileRef.get().addOnSuccessListener { result->
            var friendTot = runCatching {result[PROFILE_DOC_TOTAL] as Float}.getOrDefault(0.0f)
            var friendNGTot = runCatching { result[PROFILE_DOC_TOTAL_NG] as Float }.getOrDefault(0.0f)

            friendTot = roundToTwoDecimalPlaces(friendTot - netGive)
            friendNGTot = roundToTwoDecimalPlaces(friendNGTot - netGive)

            val updatesFriendProfile = mutableMapOf(
                PROFILE_DOC_TOTAL to friendTot,
                PROFILE_DOC_TOTAL_NG to friendNGTot
            )
            updateProfileData(friendID,updatesFriendProfile)
        }.addOnFailureListener {it->
            Log.e(TAG, "Failed to fetch data from friend Profile: $it")
        }

        updateProfileData(thisUser,updatesUserProfile)

        //Updating the friends data of both the user

        val docUserFriendRef : DocumentReference = userProfileRef
            .collection(PROFILE_DOC_FRIENDS_COLLECTION)
            .document(friendID)

        val docFriendUserRef : DocumentReference = friendProfileRef
            .collection(PROFILE_DOC_FRIENDS_COLLECTION)
            .document(thisUser)

        docUserFriendRef.get().addOnSuccessListener { result ->
            var friendTot = runCatching { (result["Balance"]  as? Double)?.toFloat() ?: 0.0f }.getOrDefault(0.0f)
            Log.i(TAG,"In this user profile -> current friend expense of $friendID is $friendTot ")

            friendTot = roundToTwoDecimalPlaces(friendTot+netGive)

            docUserFriendRef.update(
                mutableMapOf(
                    "Balance" to friendTot
                ) as Map<String, Any>
            )

            Log.i(TAG, "Fetched balance from friend total successfully")
        }

        docFriendUserRef.get().addOnSuccessListener { result ->
            var friendTot = runCatching { result["Balance"] as Float }.getOrDefault(0.0f)
            friendTot = roundToTwoDecimalPlaces(friendTot-netGive)
            docFriendUserRef.update(
                mutableMapOf(
                    "Balance" to friendTot
                ) as Map<String, Any>
            )

            Log.i(TAG, "Fetched balance from friend total successfully")
        }

        requireActivity().supportFragmentManager.commit {
            replace<FriendsFragment>(R.id.nav_host_fragment)
            addToBackStack("Friends")
        }



    }

    private fun checkIfAllGood() : Boolean {

        var expAmount : Float = 0.0f
        //Checking Expense Desc

        if (etExpDesc.text.toString().length < 3 || etExpDesc.text.toString().length > 10){
            Toast.makeText(requireContext(),"The expense description must be from 3 to 10 characters long", Toast.LENGTH_SHORT).show()
            return false
        }

        try{
            expAmount = roundToTwoDecimalPlaces(etExpAmount.text.toString().toFloat())
            if (expAmount == 0.0f){
                Toast.makeText(requireContext(),"Expenditure amount cannot be zero!",Toast.LENGTH_SHORT).show()
                return false
            }
        }catch (exception: Exception){
            Toast.makeText(requireContext(),"Invalid Expenditure amount",Toast.LENGTH_SHORT).show()
            return false
        }


        if (spPaidBy.selectedItem == "Both"){
            var total = 0.0f
            for (instance in adapterPaidBy.currentEntries){
                total += runCatching { roundToTwoDecimalPlaces(instance.text.toFloat()) }.getOrDefault(0.0f)
            }
            total = roundToTwoDecimalPlaces(total)
            if (total != expAmount){
                Toast.makeText(requireContext(), "Paid By Amount values don't add up to total", Toast.LENGTH_SHORT).show()
                return false
            }
        }

        if (spSplit.selectedItem != "Equally"){
            var total : Float = 0.0f
            for (instance in adapterSplit.currentEntries){
                total += runCatching { roundToTwoDecimalPlaces(instance.text.toFloat()) }.getOrDefault(0.0f)
            }
            total = roundToTwoDecimalPlaces(total)

            when(spSplit.selectedItem){
                "By amount" -> {
                    if (total != expAmount){
                        Toast.makeText(requireContext(), "Split amounts don't add up to expenditure amount", Toast.LENGTH_SHORT).show()
                        return false
                    }
                }

                "By percentages" -> {
                    if (total != 100.0f){
                        Toast.makeText(requireContext(),"Percentage amounts don't add up to 100%",Toast.LENGTH_SHORT).show()
                        return false
                    }
                }

                "By Stock" -> {
                    if (total == 0.0f){
                        Toast.makeText(requireContext(),"At least one user must have non-zero stock value",Toast.LENGTH_SHORT).show()
                        return false
                    }
                }
            }

        }

        return true
    }

    private fun roundToTwoDecimalPlaces(number: Float): Float {
        return (number * 100).roundToInt() / 100.0f
    }

    private fun updateTotal (tvToBeUpdated : TextView,total : Float ){
        tvToBeUpdated.text = "Remaining Amount: ${String.format("%.2f", total).toFloat()}"
        if (total < 0.0f){
            tvToBeUpdated.setTextColor(Color.parseColor("#d65c54"))
        }else{
            tvToBeUpdated.setTextColor(Color.parseColor("#000000"))
        }
    }

}