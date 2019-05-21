package com.cafedroid.android.devconnect


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.util.*


class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.window?.decorView?.systemUiVisibility= View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE
        val rootView = inflater.inflate(R.layout.fragment_splash, container, false)
        val loadingTextView: TextView = rootView.findViewById(R.id.loading_text)
        val loadingTextsArray = resources.getStringArray(R.array.loading_texts)
        loadingTextView.text = loadingTextsArray[Random().nextInt(loadingTextsArray.size)]
        return rootView
    }


}
