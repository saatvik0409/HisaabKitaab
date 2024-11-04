package com.example.spnitwise

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spnitwise.adapters.groupsAdapter
import com.example.spnitwise.utils.DISABLE_BOTTOM_MENU_LISTENER
import com.example.spnitwise.utils.dataGroups
import com.example.spnitwise.utils.dataGroupsTot
import com.example.spnitwise.utils.dataNonGroupsTot
import com.example.spnitwise.utils.dataOverallTot
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlin.math.absoluteValue

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var auth = Firebase.auth

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: groupsAdapter
    private lateinit var tvExpOverall : TextView
    private lateinit var tvExpGroup : TextView
    private lateinit var tvExpNonGroup : TextView
    private lateinit var btmNavMenu : BottomNavigationView
    private lateinit var fabNewGroup : FloatingActionButton


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        private val TAG = "HOME FRAGMENT"

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
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
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rv_fh_groups)
        tvExpOverall = view.findViewById(R.id.tv_fh_overall)
        tvExpGroup = view.findViewById(R.id.tv_fh_g)
        tvExpNonGroup = view.findViewById(R.id.tv_fh_ng)
        fabNewGroup = view.findViewById(R.id.fab_fh_newGroup)

        Log.i(TAG, "I am selecting home fragment")
        DISABLE_BOTTOM_MENU_LISTENER = true
        btmNavMenu = requireActivity().findViewById(R.id.bottom_nav)
        btmNavMenu.selectedItemId = R.id.homeFragment
        DISABLE_BOTTOM_MENU_LISTENER = false


        //TODO: Add fab listener

        if (auth.currentUser==null){
            Log.i(TAG,"auth.current user == null")
//            getActivity()?.supportFragmentManager?.popBackStack()
        }
        Log.i(TAG,"Going to call the fetchdata function now")
        setupPage();
    }

//    private fun fetchData(email: String) {
//        val docRef: DocumentReference = db.collection(USERS_COLLECTION)
//            .document(email)
//            .collection(PROFILE_COLLECTION)
//            .document(PROFILE_DOC)
//
//        docRef.get().addOnCompleteListener{task ->
//            if(task.isSuccessful){
//                Toast.makeText(getActivity(),"Fetched the document successfully",Toast.LENGTH_LONG).show()
//                Log.i(TAG, "Fetched the document from firestore successfully")
//                val document = task.result
//                var profileData = document.data
////                val groups = profileData?.get(PROFILE_DOC_GROUPS) // will always return a list of groups
//                setupPage(profileData)
//                return@addOnCompleteListener
//            }else{
//                Toast.makeText(getActivity(),"Fetched the document unsuccessfully",Toast.LENGTH_LONG).show()
//                Log.e(TAG, "Fetched the document from firestore unsuccessfully")
//            }
//        }
//    }

    private fun setupPage() {
        Log.i(TAG, "Entered the setupPage function")

        //Fetching Data if not yet fetched
//        var status : Boolean = profileDataFetched
//

        // Extract the list of strings safely
//        val groups = (profileData[PROFILE_DOC_GROUPS] as? List<String>) ?: emptyList()
//        Log.i(TAG, "Groups: $groups")
//

        // Safely extract float values with null checks and default values
//        overallTot = (profileData[PROFILE_DOC_TOTAL] as? Double)?.toFloat() ?: 0.0f
//        groupsTot = (profileData[PROFILE_DOC_TOTAL_G] as? Double)?.toFloat() ?: 0.0f
//        nonGroupTot = (profileData[PROFILE_DOC_TOTAL_NG] as? Double)?.toFloat() ?: 0.0f

        // Set up the RecyclerView adapter and layout manager
        adapter = groupsAdapter(dataGroups)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        if (dataOverallTot == 0.0f){
            tvExpOverall.text = getString(R.string.expOverallSettled)
            tvExpOverall.setTextColor(ContextCompat.getColor(requireContext(),R.color.grey))
        }else if (dataOverallTot < 0.0f){
            tvExpOverall.text = "${getString(R.string.expOverallOwe)}${dataOverallTot.absoluteValue.toString()}"
            tvExpOverall.setTextColor(ContextCompat.getColor(requireContext(),R.color.red))
        }else{
            tvExpOverall.text = "${getString(R.string.expOverallOwed)}${dataOverallTot.toString()}"
            tvExpOverall.setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
        }

        if (dataGroupsTot == 0.0f){
            tvExpGroup.text = getString(R.string.expGroupSettled)
            tvExpGroup.setTextColor(ContextCompat.getColor(requireContext(),R.color.grey))
        }else if (dataGroupsTot < 0.0f){
            tvExpGroup.text = "${getString(R.string.expGroupOwe)}${dataGroupsTot.absoluteValue.toString()}"
            tvExpGroup.setTextColor(ContextCompat.getColor(requireContext(),R.color.red))
        }else{
            tvExpGroup.text = "${getString(R.string.expGroupOwed)}${dataGroupsTot.toString()}"
            tvExpGroup.setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
        }

        if (dataNonGroupsTot == 0.0f){
            tvExpNonGroup.text = getString(R.string.expNonGroupSettled)
            tvExpNonGroup.setTextColor(ContextCompat.getColor(requireContext(),R.color.grey))
        }else if (dataNonGroupsTot < 0.0f){
            tvExpNonGroup.text = "${getString(R.string.expNonGroupOwe)}${dataNonGroupsTot.absoluteValue.toString()}"
            tvExpNonGroup.setTextColor(ContextCompat.getColor(requireContext(),R.color.red))
        }else{
            tvExpNonGroup.text = "${getString(R.string.expNonGroupOwed)}${dataNonGroupsTot.toString()}"
            tvExpNonGroup.setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
        }



        Log.i(TAG, "Setup the data and variables correctly")
    }



}