package com.cafedroid.android.devconnect

import android.app.Application
import com.androidnetworking.AndroidNetworking

class DevConnectApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidNetworking.initialize(this)
    }
}