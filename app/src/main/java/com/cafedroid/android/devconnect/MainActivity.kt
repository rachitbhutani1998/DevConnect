package com.cafedroid.android.devconnect

import android.content.Intent
import android.graphics.Color
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
import com.cafedroid.android.devconnect.adapter.OnlineListAdapter
import com.cafedroid.android.devconnect.adapter.RoomListAdapter
import com.cafedroid.android.devconnect.models.DevRoom
import com.cafedroid.android.devconnect.utils.APIResponse
import com.cafedroid.android.devconnect.utils.Constants
import com.cafedroid.android.devconnect.utils.PrefConfig
import com.pusher.chatkit.AndroidChatkitDependencies
import com.pusher.chatkit.ChatManager
import com.pusher.chatkit.ChatkitTokenProvider
import com.pusher.chatkit.CurrentUser
import com.pusher.chatkit.users.User
import com.pusher.util.Result
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    lateinit var menu: Menu
    lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mNavigationView: NavigationView

    //Fragments used in the activity
    private val splashFragment: Fragment = SplashFragment()
    private val chatFragment: Fragment = ChatFragment()

    private lateinit var onlineUserList: ArrayList<User>
    lateinit var roomsList: ArrayList<DevRoom>

    private lateinit var onlineAdapter: OnlineListAdapter
    lateinit var roomListAdapter: RoomListAdapter

    var currentRoom: DevRoom? = null
    lateinit var chatKitUser: CurrentUser
    lateinit var USER_ID: String

    lateinit var config: PrefConfig


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().add(R.id.container, splashFragment).commit()
        mDrawerLayout = findViewById(R.id.main_drawer)
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        AndroidNetworking.initialize(applicationContext)
        mNavigationView = findViewById(R.id.nav_view)
        config = PrefConfig.getInstance(this)
        val authTokenString: String? = config.getString(PrefConfig.AUTH_TOKEN, null)
        if (authTokenString == null) {
            startActivity(Intent(this, AuthActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        } else {

            val jsonObject = JSONObject(decoded(authTokenString))
            val currentUser = User(
                jsonObject.optString(APIResponse.KEY_ID), "", "", jsonObject.optString(APIResponse.KEY_NAME),
                jsonObject.optString(APIResponse.KEY_AVATAR), null, false
            )

            USER_ID = currentUser.id


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
            actionbar!!.title = getText(R.string.app_name)

            val header: View = mNavigationView.getHeaderView(0)
            val profileImageView: ImageView = header.findViewById(R.id.user_profile_image)
            val profileNameView: TextView = header.findViewById(R.id.user_profile_name)
            val signOutBtn: ImageButton = header.findViewById(R.id.sign_out_btn)
            signOutBtn.setOnClickListener {

                val alert = AlertDialog.Builder(this)
                    .setTitle("Log Out?")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("I don't want to work") { _, _ ->
                        performLogOut()
                    }.setNegativeButton("Keep Contributing") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }.create()
                alert.show()
            }

            Glide.with(this).load(currentUser.avatarURL).into(profileImageView)
            profileNameView.text = currentUser.name



            roomsListView.setOnItemClickListener { _: AdapterView<*>, v: View, i: Int, _: Long ->
                if (currentRoom?.room != roomsList[i].room) {
                    if (currentRoom != null)
                        roomsList[roomsList.indexOf(currentRoom!!)].isSelected = false
                    currentRoom = roomsList[i]
                    roomsList[i].isSelected = true
                    v.setBackgroundColor(Color.LTGRAY)
                    actionbar.title = currentRoom!!.room.name
                    supportFragmentManager.beginTransaction().detach(chatFragment).attach(chatFragment).commit()
                }
                mDrawerLayout.closeDrawers()
            }
            thread {
                val chatManager = ChatManager(
                    instanceLocator = Constants.INSTANCE_LOCATOR,
                    userId = USER_ID,
                    dependencies = AndroidChatkitDependencies(
                        tokenProvider = ChatkitTokenProvider(
                            endpoint =
                            Constants.BASE_SERVER_URL +
                                    Constants.PATH_API +
                                    Constants.PATH_AUTH +
                                    Constants.PATH_AUTHENTICATE,
                            userId = USER_ID
                        )
                    )
                )
                runOnUiThread {
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
                }
            }
        }

    }

    private fun performLogOut() {
        config.clearPreferences()
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
            R.id.menu_leave_team -> {
                leaveCurrentTeam()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun leaveCurrentTeam() {
        if (currentRoom != null) {
            val alertDialog = AlertDialog.Builder(this)
                .setTitle("Leave room?")
                .setMessage("Are you sure you want to leave ${currentRoom!!.room.name} chat room?")
                .setPositiveButton("Yeah, it is no more fun") { dialogInterface, i ->
                    chatKitUser.leaveRoom(currentRoom!!.room, callback = { result ->
                        when (result) {
                            is Result.Success -> {
                                currentRoom = null

                                supportFragmentManager.beginTransaction().replace(R.id.container, ChatFragment())
                                    .commit()
                                runOnUiThread { Toast.makeText(this, result.value, Toast.LENGTH_SHORT).show() }
                            }
                            is Result.Failure -> Toast.makeText(this, result.error.reason, Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                .setNegativeButton("I'll think about it") { dialogInterface, i ->
                    dialogInterface.dismiss()
                }
                .create()
            alertDialog.show()
        } else Toast.makeText(this, "Join a team first.", Toast.LENGTH_SHORT).show()
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
