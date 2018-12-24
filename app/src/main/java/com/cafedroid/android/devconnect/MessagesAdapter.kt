package com.cafedroid.android.devconnect

import android.content.Context
import android.support.design.R.attr.snackbarStyle
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.pusher.chatkit.CurrentUser
import com.pusher.chatkit.messages.Message
import org.joda.time.format.DateTimeFormat
import java.text.SimpleDateFormat
import java.util.*


class MessagesAdapter constructor(
    context: Context?,
    private val messages: ArrayList<Message>,
    private val sender: CurrentUser
) : RecyclerView.Adapter<MessagesAdapter.MessageHolder>() {

    private val mContext = context

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MessageHolder {
        Log.e("ADAPTER", "message by $p1 and ${sender.id}")
        return if (p1 == 0)
            MessageHolder(LayoutInflater.from(mContext).inflate(R.layout.sent_msg_layout, p0, false))
        else
            MessageHolder(LayoutInflater.from(mContext).inflate(R.layout.received_msg_layout, p0, false))
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].userId == sender.id)
            0
        else 1
    }

    override fun onBindViewHolder(msgHolder: MessageHolder, p1: Int) {
        val message = messages[p1]
        msgHolder.messageTV.text = message.text

        val simpleFormat=SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val serverTimeZone=TimeZone.getTimeZone("UTC")
        simpleFormat.timeZone=serverTimeZone
        val date=simpleFormat.parse(message.createdAt)

        val currentFormat=SimpleDateFormat("hh:mm a", Locale.getDefault())
        val currentTimeZone=TimeZone.getDefault()
        currentFormat.timeZone=currentTimeZone

        val testDate=currentFormat.format(date)

        msgHolder.timeStampTV.text=testDate
//        Glide.with(mContext).load(message.user!!.avatarURL).into(msgHolder.userImage)
        if (sender.id == message.userId) {
            msgHolder.userName.text = mContext!!.getString(R.string.you)
        } else
            msgHolder.userName.text = message.userId
    }


    class MessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //        val userImage:ImageView=itemView.findViewById(R.id.msg_u_image)
        val userName: TextView = itemView.findViewById(R.id.msg_u_name)
        val messageTV: TextView = itemView.findViewById(R.id.msg_text)
        val timeStampTV: TextView = itemView.findViewById(R.id.timestamp_tv)
    }

}