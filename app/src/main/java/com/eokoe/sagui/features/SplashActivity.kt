package com.eokoe.sagui.features

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.eokoe.sagui.R
import com.eokoe.sagui.features.categories.CategoriesActivity

class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIME = 1500L
    private val mHandler = Handler()
    private val mSplashRunnable = Runnable {
        startActivity(CategoriesActivity.getIntent(this))
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onResume() {
        super.onResume()
        mHandler.postDelayed(mSplashRunnable, SPLASH_TIME)
    }

    override fun onPause() {
        mHandler.removeCallbacks(mSplashRunnable)
        super.onPause()
    }
}
