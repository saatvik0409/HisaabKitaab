package com.example.spnitwise

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.material3.LinearProgressIndicator
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spnitwise.adapters.friendsAdapter
import com.example.spnitwise.utils.ALL_USERS_COLLECTON
import com.example.spnitwise.utils.DISABLE_BOTTOM_MENU_LISTENER
import com.example.spnitwise.utils._allUsersDataFetched
import com.example.spnitwise.utils.allSpnitwiseUsers
import com.example.spnitwise.utils.dataFriends
import com.example.spnitwise.utils.dataOverallTot
import com.example.spnitwise.utils.dbFirestore
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.absoluteValue

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FriendsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

/*
OnViewCreated -> Sets up the fragment and
NavigateToNewFriend -> Navigates to add new friend Screens
FetchAllUsers -> Fetches list of all the users present in the database


 */
class FriendsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter : friendsAdapter
    private lateinit var btmNav : BottomNavigationView
    private lateinit var fabNewFriend : FloatingActionButton
    private lateinit var tvOverall : TextView

    private var listOfAllUsers = mutableListOf<String>()

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
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rv_ff_friends)
        fabNewFriend = view.findViewById(R.id.fab_ff_addFriend)
        tvOverall = view.findViewById(R.id.tv_ff_overall)

        btmNav = requireActivity().findViewById(R.id.bottom_nav)
        DISABLE_BOTTOM_MENU_LISTENER = true
        btmNav.selectedItemId = R.id.friendsFragment
        DISABLE_BOTTOM_MENU_LISTENER = false

        adapter = friendsAdapter(dataFriends){item, index->
            val bundle = bundleOf(
                "user" to item.first,
                "index" to item
            )
            requireActivity().supportFragmentManager.commit {
                replace<SpecificFriendFragment>(R.id.nav_host_fragment,args = bundle)
                addToBackStack("SpecificFriend")
            }
        }


        if (dataOverallTot == 0.0f){
            tvOverall.text = getString(R.string.expOverallSettled)
            tvOverall.setTextColor(ContextCompat.getColor(requireContext(),R.color.grey))
        }else if (dataOverallTot < 0.0f){
            tvOverall.text = "${getString(R.string.expOverallOwe)}${dataOverallTot.absoluteValue.toString()}"
            tvOverall.setTextColor(ContextCompat.getColor(requireContext(),R.color.red))
        }else{
            tvOverall.text = "${getString(R.string.expOverallOwed)}${dataOverallTot.toString()}"
            tvOverall.setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        fabNewFriend.setOnClickListener{
            navigateToNewFriends()
        }
    }

    private fun navigateToNewFriends(){
        if (allSpnitwiseUsers.isEmpty()){
            fetchAllUsers()
            _allUsersDataFetched.observe(requireActivity()){ isFetched->
                if (isFetched){
                    requireActivity().supportFragmentManager.commit {
                        replace<NewFriendFragment>(R.id.nav_host_fragment)
                        addToBackStack("newFriends")
                    }
                }
            }
        }else{
            requireActivity().supportFragmentManager.commit {
                replace<NewFriendFragment>(R.id.nav_host_fragment)
                addToBackStack("newFriends")
            }
        }

    }
    private fun fetchAllUsers(){
        var usersCollection = dbFirestore.collection(ALL_USERS_COLLECTON)
        usersCollection.get().addOnSuccessListener {result->
            Log.i(TAG,"Number of documents fetched: ${result.size()}")
            for (document in result){
                listOfAllUsers.add(document.id)
                if (listOfAllUsers.size == result.size()){
                    allSpnitwiseUsers = listOfAllUsers
                    Log.i(TAG,"Completed Iteration $allSpnitwiseUsers")
                    _allUsersDataFetched.value = true
                }
            }
        }
        Log.i(TAG,"Fetched all users list for the first time $allSpnitwiseUsers")
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FriendsFragment.
         */
        // TODO: Rename and change types and number of parameters
        private val TAG = "FRIENDS_FRAGMENT_TAG"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FriendsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}