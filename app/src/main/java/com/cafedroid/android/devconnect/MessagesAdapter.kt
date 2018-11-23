package com.cafedroid.android.devconnect

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.pusher.chatkit.CurrentUser
import com.pusher.chatkit.messages.Message
import java.text.SimpleDateFormat
import java.util.*

class MessagesAdapter constructor(context:Context?, private val messages: ArrayList<Message>, private val sender:CurrentUser): RecyclerView.Adapter<MessagesAdapter.MessageHolder>() {

    val mContext=context

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MessageHolder {
        return MessageHolder(LayoutInflater.from(mContext).inflate(R.layout.message_layout,p0,false))
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(msgHolder: MessageHolder, p1: Int) {
        val message= messages[p1]
        msgHolder.messageTV.text=message.text
        val simpleDateFormat=SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())
        val date=Date(System.currentTimeMillis())
        val fullDateString=simpleDateFormat.format(date)
        val displayDate: String?

        Log.e("Date","${fullDateString.split(" ")[0]} and ${message.createdAt.split("T")[0]} plus ${message.text}")

        displayDate = if (fullDateString.split(" ")[0]==message.createdAt.split("T")[0])
            "Today at ${message.createdAt.slice(IntRange(9,13))}"
        else message.createdAt


        msgHolder.timeStampTV.text=displayDate
//        Glide.with(mContext).load(message.user!!.avatarURL).into(msgHolder.userImage)
        if (sender.id==message.userId) {
            msgHolder.userName.text = mContext!!.getString(R.string.you)
            msgHolder.messageTV.setBackgroundColor(mContext.resources.getColor(R.color.sentMessageBg))
        }
        else
            msgHolder.userName.text= message.userId
    }


    class MessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImage:ImageView=itemView.findViewById(R.id.msg_u_image)
        val userName:TextView=itemView.findViewById(R.id.msg_u_name)
        val messageTV:TextView=itemView.findViewById(R.id.msg_text)
        val timeStampTV:TextView=itemView.findViewById(R.id.timestamp_tv)
    }

}