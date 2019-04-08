package com.cafedroid.android.devconnect.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.cafedroid.android.devconnect.models.DevRoom
import com.pusher.chatkit.rooms.Room
import java.util.ArrayList

class RoomListAdapter internal constructor(context: Context, list: ArrayList<DevRoom>) :
    ArrayAdapter<DevRoom>(context, 0, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false)
        }
        val thisRoom = getItem(position)
        val roomNameView: TextView = listItemView!!.findViewById(android.R.id.text1)
        if (thisRoom != null) {
            roomNameView.text=thisRoom.room.name
        }
        return listItemView
    }

}