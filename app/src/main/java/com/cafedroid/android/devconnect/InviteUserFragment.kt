package com.cafedroid.android.devconnect


import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class InviteUserFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val activity= activity as AppCompatActivity
        val rootView: View = inflater.inflate(R.layout.fragment_invite_user, container, false)
        val userTextField: EditText = rootView.findViewById(R.id.username_field)
        val doneBtn: FloatingActionButton = rootView.findViewById(R.id.fab_done)
        val invitationsRV: RecyclerView = rootView.findViewById(R.id.invitation_rv)
        val sendInviteBtn: Button =rootView.findViewById(R.id.send_invite_btn)

        invitationsRV.layoutManager = GridLayoutManager(context, 2)
        val userInvitations = ArrayList<String>()
        val inviteAdapter = InviteUserAdapter(activity.applicationContext, userInvitations)
        doneBtn.setOnClickListener {
            if (!userTextField.text.toString().isEmpty()) {
                userInvitations.add(userTextField.text.toString())
                inviteAdapter.notifyDataSetChanged()
                userTextField.text.clear()
            } else Toast.makeText(context, "Field can't be empty.", Toast.LENGTH_SHORT).show()
        }
        invitationsRV.adapter=inviteAdapter

        sendInviteBtn.setOnClickListener{
            val dialog=AlertDialog.Builder(activity)
                .setTitle("Send Invites?")
                .setMessage("Are you sure you want to invite ${userInvitations.size} users to '${arguments?.getString("team_name")}'?")
                .setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                    Toast.makeText(context,"Invitations will be sent.",Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("No"){
                    dialogInterface, i -> dialogInterface.dismiss()
                }
                .create()
            dialog.show()
        }


        return rootView
    }


}
