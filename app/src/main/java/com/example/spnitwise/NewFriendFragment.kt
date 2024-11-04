package com.example.spnitwise

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.compose.animation.core.updateTransition
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spnitwise.adapters.CheckedFriends
import com.example.spnitwise.adapters.SelectableFriends
import com.example.spnitwise.utils.ALL_USERS_COLLECTON
import com.example.spnitwise.utils.PROFILE_COLLECTION
import com.example.spnitwise.utils.PROFILE_DOC
import com.example.spnitwise.utils.PROFILE_DOC_FRIENDS_COLLECTION
import com.example.spnitwise.utils.PROFILE_DOC_GROUPS_COLLECTION
import com.example.spnitwise.utils.USERS_COLLECTION
import com.example.spnitwise.utils._allUsersDataFetched
import com.example.spnitwise.utils.allSpnitwiseUsers
import com.example.spnitwise.utils.authFirestore
import com.example.spnitwise.utils.dataFriends
import com.example.spnitwise.utils.dbFirestore
import com.example.spnitwise.utils.thisUser
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NewFriendFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewFriendFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var adapterSelectedFriends : CheckedFriends
    private lateinit var adapterSelectableFriends : SelectableFriends
    private lateinit var recyclerViewSelected : RecyclerView
    private lateinit var recyclerViewSelectable : RecyclerView
    private lateinit var etSearchBar : EditText
    private lateinit var btFinish : Button

    private var selectableFriends = mutableListOf<String>()
    private var selectedFriends = mutableListOf<String>("ramesh@gmail.com")

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NewFriendFragment.
         */
        // TODO: Rename and change types and number of parameters
        private val TAG = "NEW_FRIEND_FRAGMENT_TAG"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NewFriendFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_friend, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        etSearchBar = view.findViewById(R.id.et_fnf_search)

        etSearchBar.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                updateSelectableFriends()
            }

        })

        recyclerViewSelected = view.findViewById(R.id.rv_fnf_selectedContacts)
        recyclerViewSelectable = view.findViewById(R.id.rv_fnf_selectableContacts)
        btFinish = view.findViewById(R.id.bt_fnf_finish)

        adapterSelectableFriends = SelectableFriends(selectableFriends) {item ->
            selectedFriends.add(item)
            updateSelectedFriends()
            updateSelectableFriends()
        }

        adapterSelectedFriends = CheckedFriends(selectedFriends) {item ->
            selectedFriends.remove(item)
            updateSelectedFriends()
            updateSelectableFriends()
        }

        btFinish.setOnClickListener{
            addNewFriendsToDatabase()
        }
        recyclerViewSelectable.adapter = adapterSelectableFriends
        recyclerViewSelected.adapter = adapterSelectedFriends

        recyclerViewSelectable.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewSelected.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

    }

    private fun addNewFriendsToDatabase() {
        var count = 0
        for (friend in selectedFriends){

            dataFriends.add(Pair(friend,0.0f))

            var docRef: DocumentReference =
                dbFirestore.collection(USERS_COLLECTION)
                    .document(thisUser)
                    .collection(PROFILE_COLLECTION)
                    .document(PROFILE_DOC)
                    .collection(PROFILE_DOC_FRIENDS_COLLECTION)
                    .document(friend)

            var task1 = docRef.set(
                mapOf(
                    "FriendID" to friend,
                    "Balance" to 0.0f
                ))

            docRef  =
                dbFirestore.collection(USERS_COLLECTION)
                    .document(friend)
                    .collection(PROFILE_COLLECTION)
                    .document(PROFILE_DOC)
                    .collection(PROFILE_DOC_FRIENDS_COLLECTION)
                    .document(thisUser)

            var task2 = docRef.set(
                mapOf(
                    "FriendID" to thisUser,
                    "Balance" to 0.0f
                )
            )

            Tasks.whenAllComplete(task1,task2).addOnCompleteListener{tasks->
                if (task1.isSuccessful && task2.isSuccessful){
                    Log.i(TAG,"Successfully Completed ${count}th iteration.")
                    count++
                }
                if(count == selectedFriends.size){
                    Toast.makeText(requireContext(),"Added New Friends!",Toast.LENGTH_SHORT).show()
                    Log.i(TAG,"Finished adding friends, navigating back to friends view")
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }
    }

    private fun updateSelectableFriends() {
        selectableFriends.clear()
        if (etSearchBar.text.length >= 3){
            for (user in allSpnitwiseUsers){
                if (user.contains(etSearchBar.text)
                    && user!= thisUser //don't show this user
                    && !selectedFriends.contains(user)  //don't show already selected users
                    ){
                    selectableFriends.add(user)
                }
            }
        }
        adapterSelectableFriends.notifyDataSetChanged()
    }


    private fun updateSelectedFriends(){
        adapterSelectedFriends.notifyDataSetChanged()
        btFinish.isEnabled = selectedFriends.isNotEmpty()
        btFinish.backgroundTintList = if (selectedFriends.isNotEmpty()) ContextCompat.getColorStateList(requireContext(), R.color.colorPrimaryDark) else ContextCompat.getColorStateList(requireContext(), R.color.grey)
    }


}