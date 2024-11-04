package com.example.spnitwise.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.example.spnitwise.R

class groupsAdapter(private val itemList: MutableList<Triple<String,String,Float>>) : RecyclerView.Adapter<groupsAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvGroupName: TextView = itemView.findViewById(R.id.tv_gcl_groupName)
        val tvGroupBalance: TextView = itemView.findViewById(R.id.tv_gcl_groupExpStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.groups_card_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.tvGroupName.text = currentItem.first
        val absValueExp = if (currentItem.third < 0) currentItem.third*-1 else currentItem.third
        if (currentItem.third < 0 ){
            holder.tvGroupBalance.text = "Overall, you owe ₹" + absValueExp.toString()
            holder.tvGroupBalance.setTextColor(Color.parseColor("#F10000"))
        }else if (currentItem.third > 0){
            holder.tvGroupBalance.text = "Overall, you are owed ₹" + absValueExp.toString()
            holder.tvGroupBalance.setTextColor(Color.parseColor("#30B230"))


        }else{
            holder.tvGroupBalance.text = "Overall, you are settled up!"
            holder.tvGroupBalance.setTextColor(Color.parseColor("#00F100"))
        }
    }

    override fun getItemCount() = itemList.size
}