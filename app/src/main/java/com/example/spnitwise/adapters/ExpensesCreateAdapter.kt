package com.example.spnitwise.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spnitwise.R
import com.example.spnitwise.utils.ExpenseCardModel

class ExpensesCreateAdapter (
    public val usersList : MutableList<String>,
    public val currentEntries : MutableList<ExpenseCardModel>,
    public var currentRemTotal: Float,
    public var type: String,
    private val onTextChange: (Float) -> Unit,
    ) : RecyclerView.Adapter<ExpensesCreateAdapter.ExpensesViewHolder>(){
    inner class ExpensesViewHolder (itemView: View):  RecyclerView.ViewHolder(itemView){
        val tvUserName : TextView = itemView.findViewById<TextView>(R.id.tv_aecl_user)
        val etAmount : EditText = itemView.findViewById<EditText>(R.id.et_aecl_amount)
    }

    override fun onViewRecycled(holder: ExpensesViewHolder) {
        super.onViewRecycled(holder)
        if (holder.adapterPosition < 0 || holder.adapterPosition > usersList.size){

        }else{
            if (holder.etAmount.text.toString() != ""){
                currentEntries[holder.adapterPosition].text = holder.etAmount.text.toString()
                currentRemTotal+= runCatching { holder.etAmount.text.toString().toFloat() }.getOrDefault(0.0f)
            }
            holder.etAmount.setText("")
            holder.etAmount.addTextChangedListener(null)
        }

    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExpensesCreateAdapter.ExpensesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.add_expense_card_layout,parent,false)
        return ExpensesViewHolder(view)
    }



    override fun onBindViewHolder(holder: ExpensesCreateAdapter.ExpensesViewHolder, position: Int) {
        holder.tvUserName.text = if (usersList[position].length > 6) usersList[position].substring(0,6) else usersList[position]
        val currentItem = currentEntries[position]

        if (currentItem.text!=""){
            holder.etAmount.setText(currentItem.text)
        }else{
            when (type){
                "amount" -> {
                    holder.etAmount.hint = "₹0.0"
                }
                "percentage" ->{
                    holder.etAmount.hint = "%"
                }
                "stock" -> {
                    holder.etAmount.hint = "stock"
                }
            }
        }


        //holder.etAmount.setOnFocusChangeListener(null)
        //holder.etAmount.addTextChangedListener(null)

        holder.etAmount.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                currentRemTotal += runCatching { s.toString().toFloat() }.getOrDefault(0.0f)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                currentRemTotal -= runCatching { s.toString().toFloat() }.getOrDefault(0.0f)
                currentEntries[position].text = s.toString()
                onTextChange(currentRemTotal)
            }
        })



//        if (currentItem.isFocused){
//            holder.etAmount.requestFocus()
//            holder.etAmount.setSelection(currentItem.cursorPosition)
//        }else{
//            holder.etAmount.clearFocus()
//        }



//        holder.etAmount.setOnFocusChangeListener{v, hasFocus->
//            if (hasFocus){
//                currentItem.isFocused = true
//                currentItem.cursorPosition  = holder.etAmount.selectionStart
//            }else{
//                currentItem.isFocused = false
//            }
//        }
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    fun refresh() {

    }

}