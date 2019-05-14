package com.cafedroid.android.devconnect


import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.cafedroid.android.devconnect.adapter.MessagesAdapter
import com.cafedroid.android.devconnect.models.DevRoom
import com.pusher.chatkit.messages.Direction
import com.pusher.chatkit.messages.Message
import com.pusher.chatkit.rooms.RoomListeners
import com.pusher.util.Result


class ChatFragment : Fragment() {

    private lateinit var sendBtn: ImageButton
    private lateinit var messagesAdapter: MessagesAdapter
    private var initialId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_chat, container, false)
        val emptyView: LinearLayout = rootView.findViewById(R.id.empty_view)
        val chatView: ConstraintLayout = rootView.findViewById(R.id.chat_view)
        val editText: EditText = rootView.findViewById(R.id.message_field)
        val chatLoading: ProgressBar = rootView.findViewById(R.id.loading_chat)
        val activity = activity as MainActivity

        activity.roomsList.clear()
        for (room in activity.chatKitUser.rooms) {
            activity.roomsList.add(
                DevRoom(
                    room,
                    if (activity.currentRoom != null) room == activity.currentRoom!!.room else false
                )
            )
        }
        activity.roomListAdapter.notifyDataSetChanged()

        Log.e("TAG", activity.supportActionBar!!.title.toString())

        val recyclerView: RecyclerView = rootView.findViewById(R.id.chat_messages)

        sendBtn = rootView.findViewById(R.id.send_btn)

        val layoutManager = LinearLayoutManager(context)
        layoutManager.reverseLayout = true
        recyclerView.layoutManager = layoutManager
        val messageList = ArrayList<Message>()
        messagesAdapter = MessagesAdapter(
            activity.applicationContext,
            messageList,
            activity.chatKitUser
        )
        //If user is in a room
        if (activity.currentRoom != null) {
            emptyView.visibility = View.INVISIBLE
            chatView.visibility = View.VISIBLE
            chatLoading.visibility = View.VISIBLE
            //Receive new messages
            activity.chatKitUser.subscribeToRoom(
                room = activity.currentRoom!!.room,
                listeners = RoomListeners(onMessage = { message ->
                    messageList.add(message)
                    activity.runOnUiThread {
                        messagesAdapter.notifyDataSetChanged()
                        recyclerView.invalidate()
                        recyclerView.scrollToPosition(messageList.size - 1)
                    }
                    Log.e("CHATTING", "RECEIVED.......${message.text}")

                }),
                callback = {
                    Toast.makeText(context, "Subsctibed", Toast.LENGTH_SHORT).show()
                },
                messageLimit = 0
            )

            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val manager = recyclerView.layoutManager as LinearLayoutManager
                    if (dy < 0 && manager.findLastCompletelyVisibleItemPosition() == messageList.size - 1) {
                        Log.e("ChatFragment", "load now")
                        activity.chatKitUser.fetchMessages(
                            roomId = activity.currentRoom!!.room.id,
                            direction = Direction.OLDER_FIRST,
                            initialId = messageList[initialId].id
                            ,
                            callback = { result ->
                                Log.e("CHATTING", " result")
                                if (result is Result.Success) {
                                    if (result.value.isEmpty())
                                        recyclerView.removeOnScrollListener(this)
                                    Log.e("CHATTING", "Fetching messages for ${activity.currentRoom!!.room.name}")
                                    messageList.addAll(result.value)
                                    activity.runOnUiThread {
                                        chatLoading.visibility = View.INVISIBLE
                                        recyclerView.adapter = messagesAdapter
                                        messagesAdapter.notifyDataSetChanged()
                                        recyclerView.scrollToPosition(initialId)
                                        initialId = messageList.size - 1
                                    }
                                } else if (result is Result.Failure)
                                    Toast.makeText(context, result.error.reason, Toast.LENGTH_SHORT).show()
                            })
                    }
                }
            })

            //Fetch Old messages when channel joined
            activity.chatKitUser.fetchMessages(
                roomId = activity.currentRoom!!.room.id,
                callback = { result ->
                    Log.e("CHATTING", " $result")
                    if (result is Result.Success) {
                        Log.e("CHATTING", "Fetching messages for ${activity.currentRoom!!.room.name}")
                        messageList.addAll(result.value)
                        initialId = messageList.size - 1
                        activity.runOnUiThread {
                            chatLoading.visibility = View.INVISIBLE
                            recyclerView.adapter = messagesAdapter
                            messagesAdapter.notifyDataSetChanged()
                            recyclerView.scrollToPosition(0)
                        }
                    } else if (result is Result.Failure)
                        Toast.makeText(context, result.error.reason, Toast.LENGTH_SHORT).show()
                },
                limit = 20
            )

            recyclerView.adapter = messagesAdapter
        } else {
            activity.supportActionBar!!.title = "DevConnect"
            emptyView.visibility = View.VISIBLE
            chatView.visibility = View.INVISIBLE
        }
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().trim().isNotEmpty())
                    sendBtn.visibility = View.VISIBLE
                else sendBtn.visibility = View.GONE
            }

        })

        //Send a message
        sendBtn.setOnClickListener { view ->
            if (editText.text.isNotEmpty()) {
                if (activity.currentRoom != null) {
                    activity.chatKitUser.sendMessage(
                        activity.currentRoom!!.room,
                        editText.text.toString(),
                        callback = { result ->
                            when (result) {
                                is Result.Success -> Toast.makeText(
                                    context,
                                    "Sent ${result.value}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                is Result.Failure -> Toast.makeText(
                                    context,
                                    result.error.reason,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                    editText.text.clear()
                    recyclerView.adapter = messagesAdapter
                    recyclerView.scrollToPosition(0)
                }
            }
        }
        return rootView
    }

}
