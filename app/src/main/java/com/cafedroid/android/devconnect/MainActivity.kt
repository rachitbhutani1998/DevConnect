package com.cafedroid.android.devconnect

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.Toast
import com.cafedroid.android.devconnect.classes.Users
import com.pusher.chatkit.users.User


class MainActivity : AppCompatActivity() {

    lateinit var menu: Menu
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mNavigationView: NavigationView
    private val chatFragment: Fragment = ChatFragment()

    private lateinit var onlineUserList: ArrayList<Users>
    private lateinit var onlineAdapter:OnlineListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
//        val authTokenString: String = sharedPref.getString("AuthToken", "Unavailable")
//        if (authTokenString == "Unavailable"){
//            startActivity(Intent(this,AuthActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
//            finish()
//        }


        supportFragmentManager.beginTransaction().add(R.id.container, chatFragment).commit()


        val onlineUserListView: ListView = findViewById(R.id.online_user_list)
        onlineUserList = ArrayList()
//        onlineUserList.add(
//            Users(
//                "rachitbhutani1998",
//                "Rachit Bhutani",
//                "https://avatars0.githubusercontent.com/u/20964064?s=400&v=4"
//            )
//        )
//        onlineUserList.add(
//            Users(
//                "thakursachin467",
//                "Sachin Thakur",
//                "https://pbs.twimg.com/profile_images/935543967503888384/rVfTR9NS.jpg"
//            )
//        )
//        onlineUserList.add(
//            Users(
//                "rahuldhiman93",
//                "Rahul Dhiman",
//                "https://avatars0.githubusercontent.com/u/31551130?s=460&v=4"
//            )
//        )
//        onlineUserList.add(
//            Users(
//                "rahulkathuria52",
//                "Rahul Kathuria",
//                "https://media.licdn.com/dms/image/C5603AQEYPC_sHHW7RQ/profile-displayphoto-shrink_200_200/0?e=1544659200&v=beta&t=gjbW7Z93i2FgyUJVL-ndovuqxn_VzKMAm-pGmDuXmhw"
//            )
//        )

        onlineAdapter = OnlineListAdapter(this, onlineUserList)
        onlineUserListView.adapter = onlineAdapter

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)
        }
        actionbar!!.title = "DevConnect"

        mDrawerLayout = findViewById(R.id.main_drawer)
        mNavigationView = findViewById(R.id.nav_view)

        menu = mNavigationView.menu
        menu.add(123, 0, 0, "+ Add a team")
        menu.add(123, 0, 0, "Random Team")
        menu.add(123, 0, 0, "Second Team")
        menu.add(123, 0, 0, "Third Team")

        mNavigationView.setNavigationItemSelectedListener { menuItem ->
            Log.e("MainActivity", menuItem.title.toString() + " " + menuItem.itemId)
            menuItem.isChecked = true
            when (menuItem.title) {
                "+ Add a team" -> {
                    val fragTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                    fragTransaction.addToBackStack(null)
                    fragTransaction.add(R.id.container, AddTeamDialog()).commit()
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//                    menu.add(123, 1, Menu.NONE, "Random Team")
                    menu.setGroupCheckable(123, true, true)
                }
                null -> {
                }
                else -> {
                    actionbar.title = menuItem.title
                    val bundle=Bundle()
                    bundle.putString("team_name",actionbar.title.toString())
                    chatFragment.arguments=bundle
                    supportFragmentManager.beginTransaction().detach(chatFragment).attach(chatFragment).commit()
                }
            }
            menu.setGroupCheckable(123, true, true)
            mDrawerLayout.closeDrawers()
            true
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                openDrawer()
                true
            }
            R.id.open_online_users -> {
                openOnlineDrawer()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
            mDrawerLayout.closeDrawer(GravityCompat.START)
            mDrawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
            val size = mNavigationView.menu.size()
            for (i in 0 until size) {
                mNavigationView.menu.getItem(i).isChecked = false
            }
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
    }

    fun openDrawer(v: View? = null) {
        mDrawerLayout.openDrawer(GravityCompat.START)
    }

    fun openOnlineDrawer(v: View? = null) {
        mDrawerLayout.openDrawer(GravityCompat.END)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    fun addOnlineUser(onlineUserListUpdated: ArrayList<Users>) {
//        Toast.makeText(applicationContext,onlineUserListUpdated.size.toString(),Toast.LENGTH_SHORT).show()
        onlineUserList.clear()
        onlineUserList.addAll(onlineUserListUpdated)
        onlineAdapter.notifyDataSetChanged()
    }
}
