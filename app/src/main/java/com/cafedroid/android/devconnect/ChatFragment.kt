package com.cafedroid.android.devconnect


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.cafedroid.android.devconnect.classes.Message

/**
 * A simple [Fragment] subclass.
 *
 */
class ChatFragment : Fragment() {

    lateinit var sendBtn: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_chat, container, false)
        val emptyView: LinearLayout = rootView.findViewById(R.id.empty_view)
        val chatView: RelativeLayout = rootView.findViewById(R.id.chat_view)
        val activity = activity as AppCompatActivity
        val editText: EditText = rootView.findViewById(R.id.message_field)


        val recyclerView: RecyclerView = rootView.findViewById(R.id.chat_messages)
        sendBtn = rootView.findViewById(R.id.send_btn)



        recyclerView.layoutManager = LinearLayoutManager(context)

        val messageList = ArrayList<Message>()
        messageList.add(
            Message(
                "Rahul Kathuria",
                System.currentTimeMillis(),
                "https://media.licdn.com/dms/image/C5603AQEYPC_sHHW7RQ/profile-displayphoto-shrink_200_200/0?e=1544659200&v=beta&t=gjbW7Z93i2FgyUJVL-ndovuqxn_VzKMAm-pGmDuXmhw",
                "Hello There, What's up?"
            )
        )
        val messagesAdapter = MessagesAdapter(activity.applicationContext, messageList)

        sendBtn.setOnClickListener { view ->
            if (!editText.text.isEmpty()) {
                messageList.add(
                    Message(
                        "Rachit Bhutani",
                        System.currentTimeMillis(),
                        "https://avatars0.githubusercontent.com/u/20964064?s=400&v=4",
                        editText.text.toString()
                    )
                )
                messagesAdapter.notifyDataSetChanged()
                editText.text.clear()
                recyclerView.scrollToPosition(messageList.size-1)

            }

        }
        recyclerView.adapter = messagesAdapter
        //Change the fragment when team changes
        if (activity.supportActionBar?.title!! == "DevConnect") {
            emptyView.visibility = View.VISIBLE
            chatView.visibility = View.INVISIBLE
        } else {
            emptyView.visibility = View.INVISIBLE
            chatView.visibility = View.VISIBLE
        }


        return rootView
    }


}
