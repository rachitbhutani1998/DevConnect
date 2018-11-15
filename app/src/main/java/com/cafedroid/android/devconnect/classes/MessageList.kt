package com.cafedroid.android.devconnect.classes

import android.content.Context
import com.cafedroid.android.devconnect.MessagesAdapter
import com.pusher.chatkit.messages.Message

class MessageList constructor(val context: Context?=null): ArrayList<Message>() {

    val adapter:MessagesAdapter=MessagesAdapter(context,this)

    override fun add(element: Message): Boolean {
        adapter.notifyDataSetChanged()
        return super.add(element)
    }

    override fun add(index: Int, element: Message) {
        adapter.notifyDataSetChanged()
        super.add(index, element)
    }
}