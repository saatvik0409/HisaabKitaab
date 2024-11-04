package com.example.spnitwise.utils

object ThisUserObject {

    var overallTotal : Float = 0.0f
    var nonGroupsTotal : Float = 0.0f
    var groupsTotal : Float = 0.0f
    var userName : String = ""

    var friendsList : MutableList<Pair<String,Float>> = mutableListOf()
    var groupsList : MutableList<Triple<String,String,Float>> = mutableListOf()
    var expensesList : MutableList<ExpensesData> = mutableListOf()


}