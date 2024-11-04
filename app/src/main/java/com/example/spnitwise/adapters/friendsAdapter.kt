package com.example.spnitwise.adapters

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spnitwise.R

class friendsAdapter (
    private val friendsBalanceList: MutableList<Pair<String,Float>>,
    private val onItemClick: (Pair<String,Float>, Int) -> Unit

) : RecyclerView.Adapter<friendsAdapter.friendsViewHolder>(){


    companion object{
        private val TAG = "FRIENDS_ADAPTER_TAG"
    }
    inner class friendsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvFriendName : TextView = itemView.findViewById(R.id.tv_fcl_friendName)
        val tvFriendBalance : TextView = itemView.findViewById(R.id.tv_fcl_expAmount)
        val tvFriendExpStatus : TextView = itemView.findViewById(R.id.tv_fcl_expStatus)
        fun bind(item: Pair<String,Float>, itemIndex : Int) {
            itemView.setOnClickListener {
                onItemClick(item, itemIndex)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): friendsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.friends_card_layout,parent,false)
        return friendsViewHolder(view)
    }

    override fun getItemCount() = friendsBalanceList.size

    override fun onBindViewHolder(holder: friendsViewHolder, position: Int) {
//        Log.i(TAG,"Friends List ${friendsBalanceList.get(position) is HashMap<*, *>}")
        Log.i(TAG,"Position $position")
        val currentItem = friendsBalanceList[position]
        var friendBalanceVal = currentItem.second
        holder.tvFriendName.text = if (currentItem.first.length > 10) currentItem.first.substring(0,10) else currentItem.first
//        val tryDouble : Float? = if (currentItem.second!! > 0) currentItem.second else currentItem.second !!*-1
        if (friendBalanceVal < 0){
            friendBalanceVal*=-1
        }
        holder.tvFriendBalance.text = "₹" + friendBalanceVal.toString()
        holder.tvFriendExpStatus.text = if (currentItem.second > 0) "Owes you" else "You owe"
        holder.bind(currentItem,position)
        if (currentItem.second < 0){
            holder.tvFriendExpStatus.setTextColor(Color.parseColor("#E08861"))
            holder.tvFriendBalance.setTextColor(Color.parseColor("#E08861"))
        }else{
            holder.tvFriendBalance.setTextColor(Color.parseColor("#7fcf29"))
            holder.tvFriendExpStatus.setTextColor(Color.parseColor("#7fcf29"))
        }
    }


}