package com.cafedroid.android.devconnect.utils

import android.content.Context
import android.content.SharedPreferences

class PrefConfig private constructor(context: Context) {

    private val preferences: SharedPreferences = context.getSharedPreferences("cafedroid.devconnect.preference", Context.MODE_PRIVATE)

    fun saveBoolean(key: String, value: Boolean) {
        preferences.edit()
            .putBoolean(key, value)
            .apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return preferences.getBoolean(key, defaultValue)
    }

    fun saveString(key: String, value: String?) {
        preferences.edit()
            .putString(key, value)
            .apply()
    }

    fun getString(key: String, defaultValue: String?): String? {
        return preferences.getString(key, defaultValue)
    }

    fun clearPreferences() {
        preferences.edit()
            .remove(AUTH_TOKEN)
            .apply()
    }

    companion object {

        private var prefConfig: PrefConfig? = null
        const val AUTH_TOKEN = "auth_token"

        fun getInstance(context: Context): PrefConfig {
            if (prefConfig == null)
                prefConfig = PrefConfig(context)
            return prefConfig as PrefConfig
        }
    }
}