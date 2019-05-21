package com.cafedroid.android.devconnect

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
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
import java.net.SocketTimeoutException
import java.util.concurrent.ExecutionException
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    private var retry = 1
    lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mNavigationView: NavigationView

    //Fragments used in the activity
    private val splashFragment: Fragment = SplashFragment()
    private val chatFragment: Fragment = ChatFragment()

    lateinit var roomsList: ArrayList<DevRoom>

    lateinit var roomListAdapter: RoomListAdapter

    var currentRoom: DevRoom? = null
    lateinit var chatKitUser: CurrentUser
    private lateinit var userId: String

    private lateinit var config: PrefConfig
    private lateinit var roomsListView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().add(R.id.container, splashFragment).commit()
        mDrawerLayout = findViewById(R.id.main_drawer)
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
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

            userId = currentUser.id

            roomsListView = findViewById(R.id.rooms_nav_lv)
            roomsList = ArrayList()

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
            val moreOptions: ImageButton = header.findViewById(R.id.sign_out_btn)
            if (currentRoom == null)
                moreOptions.visibility = View.GONE
            moreOptions.setOnClickListener {

                val popupMenu = PopupMenu(this, moreOptions)
                popupMenu.inflate(R.menu.popup_header_menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    if (item.itemId == R.id.menu_leave_team)
                        leaveCurrentTeam()
                    popupMenu.dismiss()
                    mDrawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                popupMenu.show()
            }

            Glide.with(this).load(currentUser.avatarURL).into(profileImageView)
            profileNameView.text = currentUser.name
            roomListAdapter = RoomListAdapter(this, roomsList)

            roomsListView.layoutManager = LinearLayoutManager(this)

            val roomChangedCallBack = object : RoomChangedCallback {
                override fun onRoomClicked(pos: Int) {
                    if (currentRoom != null && currentRoom!!.room.id == roomsList[pos].room.id) {
                        mDrawerLayout.closeDrawers()
                        return
                    }
                    if (currentRoom != null && currentRoom != roomsList[pos]) {
                        currentRoom!!.isSelected = false
                    }
                    currentRoom = roomsList[pos]
                    moreOptions.visibility = View.VISIBLE
                    roomListAdapter.notifyDataSetChanged()
                    actionbar.title = currentRoom!!.room.name
                    supportFragmentManager.beginTransaction().detach(chatFragment).attach(chatFragment).commit()
                    mDrawerLayout.closeDrawers()
                }

            }
            roomListAdapter.callback = roomChangedCallBack
            roomsListView.adapter = roomListAdapter

            try {
                connectChatManager()
            } catch (e: java.lang.Exception) {
                retry++
                if (retry <= Constants.MAX_RETRY_COUNT)
                    connectChatManager()
                else Toast
                    .makeText(this, "Unable to connect the chat, Try again later", Toast.LENGTH_LONG)
                    .show()
            }
        }

    }

    private fun logOut() {
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

    @Throws(SocketTimeoutException::class, ExecutionException::class)
    private fun connectChatManager() {
        thread {
            val chatManager = ChatManager(
                instanceLocator = Constants.INSTANCE_LOCATOR,
                userId = userId,
                dependencies = AndroidChatkitDependencies(
                    tokenProvider = ChatkitTokenProvider(
                        endpoint =
                        Constants.BASE_SERVER_URL +
                                Constants.PATH_API +
                                Constants.PATH_AUTH +
                                Constants.PATH_AUTHENTICATE,
                        userId = userId
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

    interface RoomChangedCallback {
        fun onRoomClicked(pos: Int)
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
            R.id.log_out -> {
                logOut()
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
                .setPositiveButton("Yeah, it is no more fun") { _, _ ->
                    chatKitUser.leaveRoom(currentRoom!!.room, callback = { result ->
                        when (result) {
                            is Result.Success -> {
                                currentRoom = null

                                supportFragmentManager.beginTransaction().detach(chatFragment).attach(chatFragment)
                                    .commit()
                                runOnUiThread { Toast.makeText(this, result.value, Toast.LENGTH_SHORT).show() }
                            }
                            is Result.Failure -> Toast.makeText(this, result.error.reason, Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                .setNegativeButton("I'll think about it") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .create()
            alertDialog.show()
        } else Toast.makeText(this, "Join a team first.", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
            mDrawerLayout.closeDrawer(GravityCompat.START)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
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
        val decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE)
        return String(decodedBytes)
    }


}
