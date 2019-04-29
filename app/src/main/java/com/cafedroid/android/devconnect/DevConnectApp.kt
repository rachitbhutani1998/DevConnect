package com.cafedroid.android.devconnect

import android.app.Application
import com.androidnetworking.AndroidNetworking
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric



class DevConnectApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidNetworking.initialize(this)
        Fabric.with(this, Crashlytics())
    }
}