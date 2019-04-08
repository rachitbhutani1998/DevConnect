package com.cafedroid.android.devconnect.utils

import android.content.Context
import android.widget.Toast

object NotifyUtils {

    fun createToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}
