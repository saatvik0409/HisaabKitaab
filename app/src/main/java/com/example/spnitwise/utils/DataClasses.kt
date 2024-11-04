package com.example.spnitwise.utils

import java.lang.NullPointerException

data class TimeStamp(
    var year: Int,
    var month: String,
    var day: String,
    var hour: String,
    var minutes: String
)
data class ExpensesData(
    var groupID: String,
    var timeStamp: TimeStamp,
    var amount: Float,
    var paidByMap: Map<String,Float>,
    var paidToMap: Map<String,Float>,
    var description: String
)

data class ExpenseCardModel(
    var text: String = "",
    var isFocused : Boolean = false,
    var cursorPosition : Int = 0
)

