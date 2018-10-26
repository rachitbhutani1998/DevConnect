package com.cafedroid.android.devconnect.classes

import java.text.SimpleDateFormat
import java.util.*

class Message constructor(val msgSender: String, msgTime: Long, val senderImg: String, val msgText: String){

    private val dayFormat=SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
    private val timeFormat=SimpleDateFormat("hh:mm a",Locale.ENGLISH)
    private val date:Date= Date(msgTime)
    val dateString=getDateString(date)+" at "+timeFormat.format(date)

    fun getDateString(date: Date):String{
        if (dayFormat.format(date) == dayFormat.format(Date(System.currentTimeMillis())))
            return "Today"
        else if (Integer.parseInt(dayFormat.format(date))+1==Integer.parseInt(dayFormat.format(Date(System.currentTimeMillis()))))
            return "Yesterday"
        else
            return dayFormat.format(date)
    }
}