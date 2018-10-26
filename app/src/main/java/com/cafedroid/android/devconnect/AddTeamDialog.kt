package com.cafedroid.android.devconnect


import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.*
import android.widget.EditText
import android.widget.Toast


/**
 * A simple [Fragment] subclass.
 *
 */
class AddTeamDialog : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.fragment_add_team_dialog, container, false)
        val teamField:EditText=rootView.findViewById(R.id.team_name_field)
        val fabButton: FloatingActionButton = rootView.findViewById(R.id.fab_next)
        val fabClickListener: View.OnClickListener = View.OnClickListener { view ->

            if (!teamField.text.isEmpty()) {
                val inviteFrag = InviteUserFragment()
                val bundle = Bundle()
                bundle.putString("team_name", teamField.text.toString())
                inviteFrag.arguments = bundle
                activity!!.supportFragmentManager.beginTransaction().replace(R.id.container, inviteFrag).addToBackStack(null).commit()
            } else Toast.makeText(context,"Team name mustn't be empty.",Toast.LENGTH_SHORT).show()

        }
        fabButton.setOnClickListener(fabClickListener)
        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
    }
}
