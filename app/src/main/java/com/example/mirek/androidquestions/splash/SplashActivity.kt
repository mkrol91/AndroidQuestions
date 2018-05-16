package com.example.mirek.androidquestions.splash

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.mirek.androidquestions.R
import com.example.mirek.androidquestions.util.obtainViewModel
import com.example.mirek.androidquestions.util.replaceFragmentInActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)
        replaceFragmentInActivity(obtainViewFragment(), R.id.content)
    }

    private fun obtainViewFragment() =
            supportFragmentManager.findFragmentById(R.id.content) as SplashFragment?
                    ?: SplashFragment.newInstance()

    fun obtainViewModel(): SplashViewModel = obtainViewModel(SplashViewModel::class.java)

}