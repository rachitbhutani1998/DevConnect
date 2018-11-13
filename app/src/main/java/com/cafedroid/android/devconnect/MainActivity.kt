package com.cafedroid.android.devconnect

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.bumptech.glide.Glide
import com.cafedroid.android.devconnect.classes.ChatKitLoader
import com.cafedroid.android.devconnect.classes.Users
import com.pusher.chatkit.AndroidChatkitDependencies
import com.pusher.chatkit.ChatManager
import com.pusher.chatkit.ChatkitTokenProvider
import com.pusher.chatkit.CurrentUser
import com.pusher.chatkit.users.User
import com.pusher.util.Result
import elements.Error
import org.json.JSONObject
import java.io.UnsupportedEncodingException


class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<CurrentUser> {


    lateinit var menu: Menu
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mNavigationView: NavigationView

    //Fragments used in the activity
    private val splashFragment: Fragment = SplashFragment()
    private val chatFragment: Fragment = ChatFragment()

    private lateinit var onlineUserList: ArrayList<Users>
    private lateinit var onlineAdapter: OnlineListAdapter
    lateinit var chatKitUser: CurrentUser
    lateinit var sharedPref: SharedPreferences
    lateinit var USER_ID: String
    val INSTANCE_LOCATOR: String = "v1:us1:3dd62a71-d604-4985-bbb9-5965ea8bb128"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.e("Main","started")
        supportFragmentManager.beginTransaction().add(R.id.container, splashFragment).commit()
        Log.e("Main","splashed")
        AndroidNetworking.initialize(applicationContext)

        mDrawerLayout = findViewById(R.id.main_drawer)
        mNavigationView = findViewById(R.id.nav_view)
        sharedPref = this.getSharedPreferences("TOKEN", Context.MODE_PRIVATE) ?: return
        val authTokenString: String = sharedPref.getString("auth_token", "Unavailable")
        if (authTokenString == "Unavailable") {
            startActivity(Intent(this, AuthActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        }

        val jsonObject = JSONObject(decoded(authTokenString))
        val currentUsers = Users(
            jsonObject.optString("id")
            , jsonObject.optString("name")
            , jsonObject.optString("avatar")
        )

        USER_ID = currentUsers.userName


//        supportLoaderManager.initLoader(1, null, this)


        val onlineUserListView: ListView = findViewById(R.id.online_user_list)
        onlineUserListView.emptyView = findViewById(R.id.empty_view_users)
        onlineUserList = ArrayList()

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
                    val bundle = Bundle()
                    bundle.putString("team_name", actionbar.title.toString())
                    chatFragment.arguments = bundle
                    supportFragmentManager.beginTransaction().detach(chatFragment).attach(chatFragment).commit()
                }
            }
            menu.setGroupCheckable(123, true, true)
            mDrawerLayout.closeDrawers()
            true
        }

        val header:View=mNavigationView.getHeaderView(0)
        val profileImageView:ImageView=header.findViewById(R.id.user_profile_image)
        val profileNameView:TextView=header.findViewById(R.id.user_profile_name)
        Glide.with(this).load(currentUsers.userImage).into(profileImageView)
        profileNameView.text=currentUsers.fullName
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
                    chatKitUser=result.value
                    supportFragmentManager.beginTransaction().remove(splashFragment).add(R.id.container, chatFragment).commit()
                    Log.e("TAGG","image ${chatKitUser.name}")
                }
                is Result.Failure -> Log.e("ActivityDC", result.error.reason)
                else -> Log.e("ActivityDC", "Unknown Error")
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

    fun addOnlineUser(onlineUserListUpdated: ArrayList<Users>) {
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

    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<CurrentUser> {
        return ChatKitLoader(this, USER_ID)
    }

    override fun onLoadFinished(p0: Loader<CurrentUser>, p1: CurrentUser?) {
//        chatKitUser = p1!!
//        val userProfileImage: ImageView = mNavigationView.getHeaderView(0).findViewById(R.id.user_profile_image)
//        val userProfileName: TextView = mNavigationView.getHeaderView(0).findViewById(R.id.user_profile_name)
//        Glide.with(this).load(chatKitUser.avatarURL).into(userProfileImage)
//        userProfileName.text = chatKitUser.name
        Log.e("Loader","Object Received")
        supportFragmentManager.beginTransaction().remove(splashFragment).add(R.id.container, chatFragment).commit()
    }

    override fun onLoaderReset(p0: Loader<CurrentUser>) {

    }
}
