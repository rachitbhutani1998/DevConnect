package com.cafedroid.android.devconnect

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.util.Log
import android.view.Menu

class MainActivity : AppCompatActivity() {

    lateinit var menu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mDrawerLayout: DrawerLayout = findViewById(R.id.main_drawer)
        val mNavigationView:NavigationView=findViewById(R.id.nav_view)

        menu = mNavigationView.menu
        menu.add(12,0,0,"+ Add a team")
        menu.setGroupCheckable(12,false,true)
        menu.add(123,1,Menu.NONE,"First Team")

        mNavigationView.setNavigationItemSelectedListener { menuItem ->
            Log.e("MainActivity",menuItem.title.toString() + " " + menuItem.itemId)
            menuItem.isChecked = true
            when(menuItem.title){
                "+ Add a team"->{
                    menu.add(123,1,Menu.NONE,"Random Team")
                    menu.setGroupCheckable(123,true,true)
                }
                else ->{

                }
            }
            mDrawerLayout.closeDrawers()
            true
        }
    }
}
