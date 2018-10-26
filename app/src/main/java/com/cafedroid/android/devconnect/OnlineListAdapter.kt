package com.cafedroid.android.devconnect

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.bumptech.glide.Glide
import com.cafedroid.android.devconnect.classes.Users
import de.hdodenhof.circleimageview.CircleImageView
import java.util.ArrayList

class OnlineListAdapter internal constructor(context: Context, list: ArrayList<Users>) :
    ArrayAdapter<Users>(context, 0, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.online_user_item, parent, false)
        }
        val thisUser = getItem(position)
        val imageView:CircleImageView = listItemView!!.findViewById(R.id.online_user_iv)
        val nameView:TextView= listItemView.findViewById(R.id.online_user_name)
        if (thisUser != null) {
            Glide.with(context).load(thisUser.userImage).into(imageView)
            nameView.text=thisUser.fullName
            Log.i("Adapter: ", "getView: " + thisUser.fullName)
        }
        return listItemView
    }
}