package com.cafedroid.android.devconnect.classes

import android.content.Context
import android.support.v4.content.AsyncTaskLoader
import android.util.Log
import com.pusher.chatkit.*
import com.pusher.util.Result

class ChatKitLoader constructor(context: Context, val user_id: String) : AsyncTaskLoader<CurrentUser>(context) {

    val INSTANCE_LOCATOR: String = "v1:us1:3dd62a71-d604-4985-bbb9-5965ea8bb128"

    override fun onStartLoading() {
        forceLoad()
    }

    override fun loadInBackground(): CurrentUser? {
        val chatManager = ChatManager(
            instanceLocator = INSTANCE_LOCATOR,
            userId = user_id,
            dependencies = AndroidChatkitDependencies(
                tokenProvider = ChatkitTokenProvider(
                    endpoint = "https://ancient-temple-53657.herokuapp.com/api/auth/authenticate",
                    userId = user_id
                )
            )
        )
        Log.e("Loader","connecting $user_id to chatkit")
        var chatKitResult:CurrentUser?=null
        chatManager.connect { result ->
            when(result){
                is Result.Success->Log.e("Loader",result.value.name)
                is Result.Failure->Log.e("Loader",result.error.reason)
                else ->Log.e("Loader","Unknown Error")
            }
        }
        return chatKitResult
    }

}