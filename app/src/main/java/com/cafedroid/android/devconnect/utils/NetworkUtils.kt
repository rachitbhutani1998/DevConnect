package com.cafedroid.android.devconnect.utils

import android.content.Context
import android.net.ConnectivityManager

object NetworkUtils {
    fun isNetworkAvailable(context: Context): Boolean {
        val manager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return manager.activeNetworkInfo != null && manager.activeNetworkInfo.isConnected
    }
}