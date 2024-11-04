package com.example.spnitwise.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spnitwise.R

class CheckedFriends (
    private val CheckedFriendsList : List<String>,
    private val onItemClick: (String) -> Unit

) : RecyclerView.Adapter<CheckedFriends.MyViewHolder>(){
    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val tvFriendId = itemView.findViewById<TextView>(R.id.tv_scl_contactName)

        fun bind(item: String) {
            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.selected_contact_layout,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return CheckedFriendsList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentFriend = CheckedFriendsList[position]
        holder.tvFriendId.text = if (currentFriend.length > 7) currentFriend.substring(0,7) else currentFriend
        holder.bind(currentFriend)
    }


}
