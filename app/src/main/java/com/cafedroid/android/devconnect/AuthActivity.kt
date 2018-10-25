package com.cafedroid.android.devconnect

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton

class AuthActivity : AppCompatActivity() {

    val clientId:String="3641e84228dcf2c013f7"
    val clientSecret:String="5b80b58068b439ce2c3ab86e0e8ee9f317ecd008"
    val redirectURL:String="localhost:3000/auth/github"
    val BASE_URL:String="https://github.com/login/oauth/authorize"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val githubSignInBtn: ImageButton =findViewById(R.id.github_signin_btn)
        val githubClickListener:View.OnClickListener= View.OnClickListener { view ->
            val uri:Uri=Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter("client_id",clientId).build()
            startActivity(Intent(Intent.ACTION_VIEW,uri))
        }
        githubSignInBtn.setOnClickListener(githubClickListener)
    }

    override fun onResume() {
        super.onResume()
        val uri=intent.data
        if (uri!=null)
            Log.e("This Activity",uri.toString())
    }
}
