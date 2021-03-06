package com.eokoe.sagui.features.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.features.base.view.BaseActivity
import com.eokoe.sagui.features.base.view.ViewPresenter
import com.eokoe.sagui.features.categories.CategoriesActivity
import com.eokoe.sagui.features.enterprises.EnterprisesActivity
import com.eokoe.sagui.services.upload.UploadFilesJobIntentService
import org.koin.android.ext.android.inject

class SplashActivity : BaseActivity(), SplashContract.View, ViewPresenter<SplashContract.Presenter> {
    private var mHandler: Handler? = null
    private var mSplashRunnable: Runnable? = null

    override val presenter by inject<SplashContract.Presenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onResume() {
        super.onResume()
        mHandler?.postDelayed(mSplashRunnable, SPLASH_TIME)
    }

    override fun init(savedInstanceState: Bundle?) {
        initServices()
        presenter.getEnterprise()
    }

    override fun setEnterprise(enterprise: Enterprise) {
        startActivityDelayed(CategoriesActivity.getIntent(this, enterprise))
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

    override fun initServices() {
        UploadFilesJobIntentService.enqueueWork(this)
    }

    override fun saveInstanceState(outState: Bundle) {

    }

    override fun restoreInstanceState(savedInstanceState: Bundle) {

    }

    companion object {
        val TAG = SplashActivity::class.simpleName!!
        private val SPLASH_TIME = 1500L
    }
}
