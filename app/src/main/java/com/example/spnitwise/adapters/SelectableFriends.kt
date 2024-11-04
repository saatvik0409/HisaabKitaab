package com.example.spnitwise.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spnitwise.R

class SelectableFriends (
    private val selectableFriendsList: List<String>,
    private val onItemClick: (String) -> Unit

) : RecyclerView.Adapter<SelectableFriends.MyViewHolder> (){

    companion object {
        private val TAG = "SELECTABLE_FRIENDS_TAG"
    }
    inner class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val tvFriendID = itemView.findViewById<TextView>(R.id.tv_selectable_contact)
        fun bind(item: String) {
            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.selectable_contacts_layout,parent,false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = selectableFriendsList[position]
        holder.tvFriendID.text = currentItem
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return selectableFriendsList.size
    }

    private fun itemOnClick() : String{
        Log.i(TAG,"Clicked on item")
        return "Hello World"
    }
}