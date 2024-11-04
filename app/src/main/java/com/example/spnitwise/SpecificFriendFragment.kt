package com.example.spnitwise

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.spnitwise.utils.dataFriends
import com.google.android.material.floatingactionbutton.FloatingActionButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "user"
private const val ARG_PARAM2 = "index"

/**
 * A simple [Fragment] subclass.
 * Use the [SpecificFriendFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SpecificFriendFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var thisFriendID: String? = null
    private var thisFriendIndex: Int = 0
    private var thisFriendBalance : Float = 0.0f

    private lateinit var tvFriendID : TextView
    private lateinit var tvSubHeading : TextView
    private lateinit var btSettleUp : Button
    private lateinit var fabAddExpense : FloatingActionButton

    companion object {
        private val TAG = "SPECIFIC_FRIEND_FRAGMENT_TAG"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            thisFriendID = it.getString(ARG_PARAM1)
            thisFriendIndex = it.getInt(ARG_PARAM2)
        }
        thisFriendBalance = dataFriends[thisFriendIndex].second
        Log.i(TAG,"thisFriendID: $thisFriendID")
        Log.i(TAG,"thisFriendBalance: $thisFriendIndex")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_specific_friend, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvFriendID = view.findViewById(R.id.tv_fsf_friendUserID)
        tvSubHeading = view.findViewById(R.id.tv_fsf_friendBalanceAmount)
        btSettleUp = view.findViewById(R.id.bt_fsf_friendExpenseSettleUp)
        fabAddExpense = view.findViewById(R.id.fab_fsf_addExpense)

        tvFriendID.text = thisFriendID
        btSettleUp.isEnabled = true
        btSettleUp.backgroundTintList = ContextCompat.getColorStateList(requireContext(),R.color.colorPrimaryDark)

        if (thisFriendBalance > 0){
            tvSubHeading.text = "They owe you ₹"+thisFriendBalance.toString()
            tvSubHeading.setTextColor(Color.parseColor("#7ec23e"))
        }else if (thisFriendBalance < 0){
            tvSubHeading.text = "You owe them ₹"+(thisFriendBalance*-1).toString()
            tvSubHeading.setTextColor(Color.parseColor("#e8602a"))
        }else{
            tvSubHeading.text = "You both are settled up!"
            btSettleUp.isEnabled = false
            btSettleUp.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.lightgrey)
        }

        fabAddExpense.setOnClickListener{
            requireActivity().supportFragmentManager.commit {
                val bundle = bundleOf(
                    "friendID" to thisFriendID,
                    "friendIndex" to thisFriendIndex
                )
                replace<AddFriendExpenseFragment>(R.id.nav_host_fragment,args = bundle)
                addToBackStack("SpecificFriendFragment")
            }
        }



    }

}