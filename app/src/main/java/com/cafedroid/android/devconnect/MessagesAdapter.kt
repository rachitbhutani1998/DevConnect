package com.cafedroid.android.devconnect

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.pusher.chatkit.messages.Message

class MessagesAdapter constructor(context:Context?, val messages: ArrayList<Message>): RecyclerView.Adapter<MessagesAdapter.MessageHolder>() {

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
        msgHolder.timeStampTV.text=message.createdAt
        Log.e("MessageADAPTER",messages.size.toString())
//        Glide.with(mContext).load(message.user!!.avatarURL).into(msgHolder.userImage)
        msgHolder.userName.text= message.userId
    }


    class MessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImage:ImageView=itemView.findViewById(R.id.msg_u_image)
        val userName:TextView=itemView.findViewById(R.id.msg_u_name)
        val messageTV:TextView=itemView.findViewById(R.id.msg_text)
        val timeStampTV:TextView=itemView.findViewById(R.id.timestamp_tv)
    }

}