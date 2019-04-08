package com.cafedroid.android.devconnect

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.cafedroid.android.devconnect.utils.*
import org.json.JSONObject


class AuthActivity : AppCompatActivity() {

    lateinit var progressSpinner: ProgressBar
    lateinit var githubSignInBtn: TextView
    lateinit var prefConfig: PrefConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        prefConfig = PrefConfig.getInstance(this)
        githubSignInBtn = findViewById(R.id.github_signin_btn)
        progressSpinner = findViewById(R.id.auth_spinner)
        progressSpinner.visibility = View.INVISIBLE
        val githubClickListener: View.OnClickListener = View.OnClickListener {
            val uri: Uri = Uri.parse(Constants.GITHUB_BASE_URL).buildUpon()
                .appendQueryParameter(APIResponse.KEY_CLIENT_ID, Constants.CLIENT_ID).build()
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
        githubSignInBtn.setOnClickListener(githubClickListener)
    }

    override fun onResume() {
        super.onResume()
        val uri = intent.data
        if (uri != null) {
            progressSpinner.visibility = View.VISIBLE
            githubSignInBtn.visibility = View.INVISIBLE
            val code = uri.getQueryParameter(APIResponse.KEY_CODE)
            if (NetworkUtils.isNetworkAvailable(this))
                AndroidNetworking.get(
                    Constants.BASE_SERVER_URL +
                        "${Constants.PATH_API}${Constants.PATH_AUTH}${Constants.PATH_GITHUB}")

                    .addQueryParameter(APIResponse.KEY_CODE, code)
                    .build().getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject?) {
                            val token = response!!.optString(APIResponse.KEY_TOKEN)
                            prefConfig.saveString(PrefConfig.AUTH_TOKEN, token)
                            navigateToMainActivity()
                        }

                        override fun onError(anError: ANError?) {
                            progressSpinner.visibility = View.INVISIBLE
                            githubSignInBtn.visibility = View.VISIBLE
                            Log.e("Response", anError?.message)
                            Toast.makeText(applicationContext, anError?.message, Toast.LENGTH_LONG).show()
                        }
                    })
            else NotifyUtils.createToast(this,"Check your internet connection & try again.")
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(applicationContext, MainActivity::class.java))
        finish()
    }
}
