package com.cafedroid.android.devconnect.models

import com.pusher.chatkit.rooms.Room

class DevRoom(var room : Room, var isSelected: Boolean = false){
    override fun equals(other: Any?): Boolean {
        if (other is DevRoom && other.room.id == this.room.id)
            return true
        return false
    }

    override fun hashCode(): Int {
        return room.hashCode()
    }
}