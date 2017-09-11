package com.eokoe.sagui.features.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.data.model.impl.SurveyModelImpl
import com.eokoe.sagui.features.base.view.BaseActivity
import com.eokoe.sagui.features.base.view.ViewPresenter
import com.eokoe.sagui.features.dashboard.DashboardActivity
import com.eokoe.sagui.features.enterprises.EnterprisesActivity

class SplashActivity : BaseActivity(), SplashContract.View, ViewPresenter<SplashContract.Presenter> {

    private val SPLASH_TIME = 1500L
    private var mHandler: Handler? = null
    private var mSplashRunnable: Runnable? = null

    override lateinit var presenter: SplashContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onResume() {
        super.onResume()
        mHandler?.postDelayed(mSplashRunnable, SPLASH_TIME)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        super.setUp(savedInstanceState)
        presenter = SplashPresenter(SurveyModelImpl())
    }

    override fun init(savedInstanceState: Bundle?) {
        presenter.getEnterprise()
    }

    override fun setEnterprise(enterprise: Enterprise) {
        startActivityDelayed(DashboardActivity.getIntent(this, enterprise))
    }

    override fun onEmptyEnterprise() {
        startActivityDelayed(EnterprisesActivity.getIntent(this))
    }

    private fun startActivityDelayed(intent: Intent) {
        if (mHandler == null) {
            mHandler = Handler()
        }
        mSplashRunnable = Runnable {
            startActivity(intent)
        }
        mHandler?.postDelayed(mSplashRunnable, SPLASH_TIME)
    }

    override fun onPause() {
        mHandler?.removeCallbacks(mSplashRunnable)
        super.onPause()
    }
}