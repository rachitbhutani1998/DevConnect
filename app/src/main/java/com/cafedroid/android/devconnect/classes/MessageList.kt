package com.cafedroid.android.devconnect.classes

import android.content.Context
import com.cafedroid.android.devconnect.MessagesAdapter
import com.pusher.chatkit.messages.Message

class MessageList constructor(val context: Context?=null): ArrayList<Message>()