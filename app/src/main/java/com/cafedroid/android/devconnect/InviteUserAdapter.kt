package com.cafedroid.android.devconnect

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView

class InviteUserAdapter constructor(private val context: Context, private val usernames: ArrayList<String>) :
    RecyclerView.Adapter<InviteUserAdapter.InvitationViewHolder>() {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): InvitationViewHolder {
        return InvitationViewHolder(LayoutInflater.from(context).inflate(R.layout.invitation_single, p0, false))
    }

    override fun getItemCount(): Int {
        return usernames.size
    }

    override fun onBindViewHolder(p0: InvitationViewHolder, p1: Int) {
        val userName: String = usernames[p1]
        p0.usernameTV.text = userName
        p0.removeInviteBtn.setOnClickListener {
            usernames.remove(userName)
            this.notifyDataSetChanged()
        }
    }

    class InvitationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTV: TextView = itemView.findViewById(R.id.invite_uname)
        val removeInviteBtn: ImageButton = itemView.findViewById(R.id.cancel_invite)
    }
}