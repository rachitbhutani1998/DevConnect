package com.cafedroid.android.devconnect

import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.content.Context.MODE_PRIVATE
import android.content.Intent


class MainActivity : AppCompatActivity() {

    lateinit var menu: Menu
    private lateinit var mDrawerLayout: DrawerLayout
    lateinit var mNavigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        val authTokenString:String=sharedPref.getString("AuthToken","Unavailable")
        if (authTokenString == "Unavailable"){
            startActivity(Intent(this,AuthActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        }



        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)
        }

        mDrawerLayout = findViewById(R.id.main_drawer)
        mNavigationView = findViewById(R.id.nav_view)

        menu = mNavigationView.menu
        menu.add(123, 0, 0, "+ Add a team")

        mNavigationView.setNavigationItemSelectedListener { menuItem ->
            Log.e("MainActivity", menuItem.title.toString() + " " + menuItem.itemId)
            menuItem.isChecked = true
            when (menuItem.title) {
                "+ Add a team" -> {
                    val fragTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                    val prev: Fragment? = supportFragmentManager.findFragmentByTag("dialog")
                    if (prev != null) {
                        fragTransaction.remove(prev)
                    }
                    fragTransaction.addToBackStack(null)
                    fragTransaction.add(R.id.container, AddTeamDialog(), "dialog").commit()
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//                    menu.add(123, 1, Menu.NONE, "Random Team")
                    menu.setGroupCheckable(123, true, true)
                }
                else -> {
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
                mDrawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
}
