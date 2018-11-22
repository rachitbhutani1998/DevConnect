package com.cafedroid.android.devconnect

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.androidnetworking.AndroidNetworking
import com.bumptech.glide.Glide
import com.pusher.chatkit.AndroidChatkitDependencies
import com.pusher.chatkit.ChatManager
import com.pusher.chatkit.ChatkitTokenProvider
import com.pusher.chatkit.CurrentUser
import com.pusher.chatkit.rooms.Room
import com.pusher.chatkit.users.User
import com.pusher.util.Result
import org.json.JSONObject
import java.io.UnsupportedEncodingException


class MainActivity : AppCompatActivity() {

    lateinit var menu: Menu
    lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mNavigationView: NavigationView

    //Fragments used in the activity
    private val splashFragment: Fragment = SplashFragment()
    private val chatFragment: Fragment = ChatFragment()

    private lateinit var onlineUserList: ArrayList<User>
    lateinit var roomsList: ArrayList<Room>

    private lateinit var onlineAdapter: OnlineListAdapter
    lateinit var roomListAdapter: RoomListAdapter

    var currentRoom: Room? = null
    lateinit var chatKitUser: CurrentUser
    lateinit var sharedPref: SharedPreferences
    lateinit var USER_ID: String
    val INSTANCE_LOCATOR: String = "v1:us1:3dd62a71-d604-4985-bbb9-5965ea8bb128"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.e("Main", "started")
        supportFragmentManager.beginTransaction().add(R.id.container, splashFragment).commit()
        mDrawerLayout = findViewById(R.id.main_drawer)
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        Log.e("Main", "splashed")
        AndroidNetworking.initialize(applicationContext)

        mNavigationView = findViewById(R.id.nav_view)
        sharedPref = this.getSharedPreferences("TOKEN", Context.MODE_PRIVATE) ?: return
        val authTokenString: String = sharedPref.getString("auth_token", "Unavailable")
        if (authTokenString == "Unavailable") {
            startActivity(Intent(this, AuthActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        } else {

            val jsonObject = JSONObject(decoded(authTokenString))
            val currentUsers=User(jsonObject.optString("id"),"","",jsonObject.optString("name"),
                jsonObject.optString("avatar"),null,false)

            USER_ID = currentUsers.id


            val onlineUserListView: ListView = findViewById(R.id.online_user_list)
            onlineUserListView.emptyView = findViewById(R.id.empty_view_users)
            onlineUserList = ArrayList()

            val roomsListView: ListView = findViewById(R.id.rooms_nav_lv)
            roomsList = ArrayList()
            roomListAdapter = RoomListAdapter(this, roomsList)
            roomsListView.adapter = roomListAdapter

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

            val header: View = mNavigationView.getHeaderView(0)
            val profileImageView: ImageView = header.findViewById(R.id.user_profile_image)
            val profileNameView: TextView = header.findViewById(R.id.user_profile_name)
            val signOutBtn:ImageButton=header.findViewById(R.id.sign_out_btn)
            signOutBtn.setOnClickListener{

                val alert=AlertDialog.Builder(this)
                    .setTitle("Log Out?")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("I don't want to work") { dialogInterface, i ->
                        val sharedPrefEditor=sharedPref.edit()
                        with(sharedPrefEditor){
                            putString("auth_token","Unavailable")
                            apply()
                        }
                        val token: String = sharedPref.getString("auth_token", "Unavailable")
                        if (token == "Unavailable") {
                            startActivity(Intent(this, AuthActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                            finish()
                        }
                    }.setNegativeButton("Keep Contributing"){dialogInterface, i ->
                        dialogInterface.dismiss()
                    }.create()
                alert.show()
            }

            Glide.with(this).load(currentUsers.avatarURL).into(profileImageView)
            profileNameView.text = currentUsers.name
            val chatManager = ChatManager(
                instanceLocator = INSTANCE_LOCATOR,
                userId = USER_ID,
                dependencies = AndroidChatkitDependencies(
                    tokenProvider = ChatkitTokenProvider(
                        endpoint = "https://ancient-temple-53657.herokuapp.com/api/auth/authenticate",
                        userId = USER_ID
                    )
                )
            )
            chatManager.connect { result ->
                when (result) {
                    is Result.Success -> {
                        chatKitUser = result.value
                        supportFragmentManager.beginTransaction().remove(splashFragment)
                            .add(R.id.container, chatFragment).commit()
                        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                    }
                    is Result.Failure -> Log.e("ActivityDC", result.error.reason)
                    else -> Log.e("ActivityDC", "Unknown Error")
                }
            }


            roomsListView.setOnItemClickListener { _: AdapterView<*>, _: View, i: Int, _: Long ->
                if (currentRoom != roomsList[i]) {
                    currentRoom = roomsList[i]
                    actionbar.title = currentRoom!!.name
                    supportFragmentManager.beginTransaction().detach(chatFragment).attach(chatFragment).commit()
                }
                mDrawerLayout.closeDrawers()
            }
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

    fun addOnlineUser(onlineUserListUpdated: ArrayList<User>) {
        onlineUserList.clear()
        onlineUserList.addAll(onlineUserListUpdated)
        onlineAdapter.notifyDataSetChanged()
    }


    //Decoding JWT code
    @Throws(Exception::class)
    fun decoded(JWTEncoded: String): String {
        try {
            val split = JWTEncoded.substring(7).split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return getJson(split[1])
        } catch (e: UnsupportedEncodingException) {
            //Error
        }
        return "Error 404. Data not found."
    }

    @Throws(UnsupportedEncodingException::class)
    private fun getJson(strEncoded: String): String {
        val decodedBytes = android.util.Base64.decode(strEncoded, Base64.URL_SAFE)
        return String(decodedBytes)
    }

}
