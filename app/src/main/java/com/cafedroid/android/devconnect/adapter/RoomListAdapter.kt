package com.cafedroid.android.devconnect.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.cafedroid.android.devconnect.MainActivity
import com.cafedroid.android.devconnect.R
import com.cafedroid.android.devconnect.models.DevRoom

class RoomListAdapter internal constructor(val context: Context, val roomList:ArrayList<DevRoom>) :
    RecyclerView.Adapter<RoomListAdapter.RoomViewHolder>() {

    lateinit var callback: MainActivity.RoomChangedCallback

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): RoomViewHolder {
        return RoomViewHolder(LayoutInflater.from(context).inflate(R.layout.room_item_layout, viewGroup, false))
    }

    override fun getItemCount(): Int {
        return roomList.size
    }

    override fun onBindViewHolder(viewHolder: RoomViewHolder, pos: Int) {
        val thisRoom = roomList[pos]
        viewHolder.roomNameView.text = thisRoom.room.name
        viewHolder.roomNameView.isEnabled = !thisRoom.isSelected
        viewHolder.bind()
    }

    inner class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val roomNameView: TextView = itemView.findViewById(R.id.room_name_tv)
        val msgIndicator: ImageView = itemView.findViewById(R.id.msg_indicator_iv)
        private val roomContainer: RelativeLayout = itemView.findViewById(R.id.room_nav_container)

        fun bind() {
            roomContainer.setOnClickListener {
                roomList[adapterPosition].isSelected = true
                callback.onRoomClicked(adapterPosition)
            }
        }

    }

}