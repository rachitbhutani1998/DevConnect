package com.cafedroid.android.devconnect

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
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
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*


class AuthActivity : AppCompatActivity() {

    val clientId: String = "3641e84228dcf2c013f7"
    val clientSecret: String = "5b80b58068b439ce2c3ab86e0e8ee9f317ecd008"
    val redirectURL: String = "localhost:3000/auth/github"
    val BASE_URL: String = "https://github.com/login/oauth/authorize"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        val githubSignInBtn: ImageButton = findViewById(R.id.github_signin_btn)
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
            val code = uri.getQueryParameter("code")
            AndroidNetworking.get("https://ancient-temple-53657.herokuapp.com/api/auth/github")
                .addQueryParameter("code", code)
                .build().getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject?) {
                        val token=response!!.optString("token").substring(7)
//                        Log.e("Response", decoded(token))
                        decoded(token)
                    }

                    override fun onError(anError: ANError?) {
                        Log.e("Response", anError?.message)
                    }
                })


        }
    }


    //Decoding JWT code
    @Throws(Exception::class)
    fun decoded(JWTEncoded: String) :String{
        try {
            val split = JWTEncoded.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            Log.d("JWT_DECODED", "Body: " + getJson(split[1]))
            return getJson(split[1])
        } catch (e: UnsupportedEncodingException) {
            //Error
        }
        return "Error 404. Data not found."
    }

    @Throws(UnsupportedEncodingException::class)
    private fun getJson(strEncoded: String): String {
        val decodedBytes = android.util.Base64.decode(strEncoded, Base64.URL_SAFE)
        return String(decodedBytes)
    }
}
