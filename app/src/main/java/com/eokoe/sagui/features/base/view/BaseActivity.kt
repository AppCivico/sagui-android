package com.eokoe.sagui.features.base.view

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.IntentCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.crashlytics.android.Crashlytics
import com.eokoe.sagui.BuildConfig
import com.eokoe.sagui.features.base.presenter.BasePresenter
import io.fabric.sdk.android.Fabric

/**
 * @author Pedro Silva
 * @since 16/08/17
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, Crashlytics())
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setUp(savedInstanceState)
        if (this is ViewPresenter<*>) {
            val view = this as ViewPresenter<BasePresenter<Any>>
            view.presenter.attach(this)
        }
        init(savedInstanceState)
    }

    override fun onDestroy() {
        if (this is ViewPresenter<*>) {
            presenter.detach()
        }
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun startActivityAndClearStack(intent: Intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or IntentCompat.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    open fun setUp(savedInstanceState: Bundle?) {

    }

    abstract fun init(savedInstanceState: Bundle?)
}