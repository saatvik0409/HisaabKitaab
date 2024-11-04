package com.example.spnitwise.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spnitwise.R
import com.example.spnitwise.utils.ExpensesData
import com.example.spnitwise.utils.thisUser

class FriendExpensesAdapter(
    private val friendExpensesList : MutableList<ExpensesData>,
    private val thisFriendID: String
) : RecyclerView.Adapter<FriendExpensesAdapter.FriendsExpensesViewHolder> (){
    inner class FriendsExpensesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tvExpenseTitle : TextView = itemView.findViewById<TextView>(R.id.tv_ecl_expTitle)
        val tvExpenseAmount: TextView = itemView.findViewById<TextView>(R.id.tv_ecl_expAmount)
        val tvExpenseOwedOwe : TextView = itemView.findViewById<TextView>(R.id.tv_ecl_owedOrOwe)
        val tvExpenseDesc : TextView= itemView.findViewById<TextView>(R.id.tv_ecl_expDesc)
        val tvExpenseMonth : TextView = itemView.findViewById<TextView>(R.id.tv_ecl_dateMonth)
        val tvExpensesDay : TextView= itemView.findViewById<TextView>(R.id.tv_ecl_dateDay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsExpensesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.expenses_card_layout,parent,false)
        return FriendsExpensesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return friendExpensesList.size
    }

    override fun onBindViewHolder(holder: FriendsExpensesViewHolder, position: Int) {
        val thisExpense : ExpensesData = friendExpensesList[position]

        holder.tvExpenseTitle.text = thisExpense.description

        var thisUserPaid : Float = thisExpense.paidByMap[thisUser] ?: 0.0f
        var thisUserGotPaid : Float = thisExpense.paidToMap[thisUser] ?: 0.0f

        val thisUserNett = thisUserPaid - thisUserGotPaid

        if (thisUserNett < 0){
            holder.tvExpenseOwedOwe.text = "You borrowed"
            holder.tvExpenseAmount.text = "₹"+(thisUserNett*-1).toString()
            if (thisExpense.groupID == ""){
                holder.tvExpenseDesc.text = thisFriendID + " lended you"
            }

            holder.tvExpenseAmount.setTextColor(Color.parseColor("#e8602a"))
            holder.tvExpenseOwedOwe.setTextColor(Color.parseColor("#e8602a"))
        }else if (thisUserNett > 0){
            holder.tvExpenseOwedOwe.text = "You lended"
            holder.tvExpenseAmount.text = "₹"+(thisUserNett).toString()
            if (thisExpense.groupID == ""){
                holder.tvExpenseDesc.text = "You lended " +thisFriendID
            }

            holder.tvExpenseAmount.setTextColor(Color.parseColor("#7ec23e"))
            holder.tvExpenseOwedOwe.setTextColor(Color.parseColor("#7ec23e"))

            holder.tvExpensesDay.text = thisExpense.timeStamp.day
            holder.tvExpenseMonth.text = thisExpense.timeStamp.month


        }




    }

}