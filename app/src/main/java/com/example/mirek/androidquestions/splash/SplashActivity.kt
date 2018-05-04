package com.example.mirek.androidquestions.splash

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.mirek.androidquestions.R
import com.example.mirek.androidquestions.util.replaceFragmentInActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)

        val splashFragment = supportFragmentManager.findFragmentById(R.id.content) as SplashFragment?
                ?: SplashFragment.newInstance().also { replaceFragmentInActivity(it, R.id.content) }
    }

}