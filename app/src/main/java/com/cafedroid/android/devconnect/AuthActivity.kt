package com.cafedroid.android.devconnect

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.SigningInfo
import android.net.Uri
import android.opengl.Visibility
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.error.ANError
import org.json.JSONObject
import org.json.JSONArray
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.auth0.android.jwt.JWT
import android.util.Base64.URL_SAFE
import android.widget.ProgressBar
import android.widget.TextView
import org.w3c.dom.Text
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*


class AuthActivity : AppCompatActivity() {

    val clientId: String = "3641e84228dcf2c013f7"
    val BASE_URL: String = "https://github.com/login/oauth/authorize"
    lateinit var progressSpinner:ProgressBar
    lateinit var githubSignInBtn:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        githubSignInBtn = findViewById(R.id.github_signin_btn)
        progressSpinner=findViewById(R.id.auth_spinner)
        progressSpinner.visibility=View.INVISIBLE
        val githubClickListener: View.OnClickListener = View.OnClickListener {
            val uri: Uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter("client_id", clientId).build()
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
        githubSignInBtn.setOnClickListener(githubClickListener)
    }

    override fun onResume() {
        super.onResume()
        val uri = intent.data
        if (uri != null) {
            progressSpinner.visibility=View.VISIBLE
            githubSignInBtn.visibility=View.INVISIBLE
            val code = uri.getQueryParameter("code")
            AndroidNetworking.get("https://ancient-temple-53657.herokuapp.com/api/auth/github")
                .addQueryParameter("code", code)
                .build().getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject?) {
                        val token=response!!.optString("token")
                        val sharedPref=applicationContext.getSharedPreferences("TOKEN",Context.MODE_PRIVATE)
                        val sharedPrefEditor=sharedPref.edit()
                        with(sharedPrefEditor){
                            putString("auth_token",token)
                            apply()
                            progressSpinner.visibility=View.INVISIBLE
                            startActivity(Intent(applicationContext,MainActivity::class.java))
                            finish()
                        }
                    }

                    override fun onError(anError: ANError?) {
                        progressSpinner.visibility=View.INVISIBLE
                        githubSignInBtn.visibility=View.VISIBLE
                        Log.e("Response", anError?.message)
                    }
                })
        }
    }
}
