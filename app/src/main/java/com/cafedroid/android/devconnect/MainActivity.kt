package com.cafedroid.android.devconnect

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem

class MainActivity : AppCompatActivity() {

    lateinit var menu: Menu
    private lateinit var mDrawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)
        }

        mDrawerLayout = findViewById(R.id.main_drawer)
        val mNavigationView: NavigationView = findViewById(R.id.nav_view)

        menu = mNavigationView.menu
        menu.add(12, 0, 0, "+ Add a team")
        menu.setGroupCheckable(12, false, true)
        menu.add(123, 1, Menu.NONE, "First Team")

        mNavigationView.setNavigationItemSelectedListener { menuItem ->
            Log.e("MainActivity", menuItem.title.toString() + " " + menuItem.itemId)
            menuItem.isChecked = true
            when (menuItem.title) {
                "+ Add a team" -> {
                    menu.add(123, 1, Menu.NONE, "Random Team")
                }
                else -> {
                    menu.setGroupCheckable(123, true, true)
                }
            }
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
}
