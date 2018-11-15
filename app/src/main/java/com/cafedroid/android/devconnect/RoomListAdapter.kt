package com.cafedroid.android.devconnect

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.pusher.chatkit.rooms.Room
import java.util.ArrayList

class RoomListAdapter internal constructor(context: Context, list: ArrayList<Room>) :
    ArrayAdapter<Room>(context, 0, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false)
        }
        val thisRoom = getItem(position)
        val roomNameView: TextView = listItemView!!.findViewById(android.R.id.text1)
        if (thisRoom != null) {
            roomNameView.text=thisRoom.name
        }
        return listItemView
    }

    override fun setNotifyOnChange(notifyOnChange: Boolean) {
        super.setNotifyOnChange(notifyOnChange)
        Toast.makeText(context,"Notified",Toast.LENGTH_SHORT).show()
    }
}