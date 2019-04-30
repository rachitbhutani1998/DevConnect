package com.cafedroid.android.devconnect.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.cafedroid.android.devconnect.R
import com.pusher.chatkit.CurrentUser
import com.pusher.chatkit.messages.Message
import java.text.SimpleDateFormat
import java.util.*


class MessagesAdapter constructor(
    context: Context?,
    private val messages: ArrayList<Message>,
    private val sender: CurrentUser
) : RecyclerView.Adapter<MessagesAdapter.MessageHolder>() {

    private val mContext = context

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MessageHolder {
        return if (p1 == 0)
            MessageHolder(
                LayoutInflater.from(mContext).inflate(
                    R.layout.sent_msg_layout,
                    p0,
                    false
                )
            )
        else
            MessageHolder(
                LayoutInflater.from(mContext).inflate(
                    R.layout.received_msg_layout,
                    p0,
                    false
                )
            )
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            messages[position].userId == sender.id -> 0
            else -> 1
        }
    }

    override fun onBindViewHolder(msgHolder: MessageHolder, p1: Int) {
        val message = messages[p1]
        msgHolder.messageTV.text = message.text

        val simpleFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val serverTimeZone = TimeZone.getTimeZone("UTC")
        simpleFormat.timeZone = serverTimeZone
        val date = simpleFormat.parse(message.createdAt)

        val currentFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val currentTimeZone = TimeZone.getDefault()
        currentFormat.timeZone = currentTimeZone

        val testDate = currentFormat.format(date)
        if (p1 > 0 && messages[p1 - 1].userId == message.userId) {
            msgHolder.userName.visibility = View.GONE
            msgHolder.userImage.visibility = View.GONE
        } else {
            msgHolder.userName.visibility = View.VISIBLE
            msgHolder.userImage.visibility = View.VISIBLE
            msgHolder.timeStampTV.visibility = View.VISIBLE
        }
        if (p1 + 1 < messages.size && messages[p1 + 1].userId == message.userId && simpleFormat.parse(messages[p1 - 1].createdAt).time - date.time < 60000)
            msgHolder.timeStampTV.visibility = View.GONE
        else
            msgHolder.timeStampTV.visibility = View.VISIBLE

        msgHolder.bind()
        msgHolder.timeStampTV.text = testDate
        msgHolder.userImage.text = message.userId[0].toString().toUpperCase()
//        Glide.with(mContext).load(message.user!!.avatarURL).into(msgHolder.userImage)
        if (sender.id != message.userId)
            msgHolder.userName.text = message.userId
    }


    inner class MessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {
            val androidColors = mContext!!.resources.getIntArray(R.array.androidcolors)
            val randomAndroidColor = androidColors[Random().nextInt(androidColors.size)]
            userImage.backgroundTintList = ColorStateList.valueOf(randomAndroidColor)
            messageTV.setOnClickListener {
                timeStampTV.visibility = if (timeStampTV.visibility == View.GONE) View.VISIBLE else View.GONE
            }
        }

        val userImage: TextView = itemView.findViewById(R.id.msg_u_image)
        val userName: TextView = itemView.findViewById(R.id.msg_u_name)
        val messageTV: TextView = itemView.findViewById(R.id.msg_text)
        val timeStampTV: TextView = itemView.findViewById(R.id.timestamp_tv)
    }

}