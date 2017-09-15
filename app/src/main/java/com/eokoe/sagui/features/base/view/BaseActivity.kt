package com.eokoe.sagui.features.base.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.IntentCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.eokoe.sagui.features.base.presenter.BasePresenter
import com.eokoe.sagui.utils.LogUtil


/**
 * @author Pedro Silva
 * @since 16/08/17
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtil.startCrashlytics(this)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setUp(savedInstanceState)
        val view = this as? ViewPresenter<BasePresenter<Any>>
        view?.presenter?.attach(this)
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

    open fun showBackButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    open fun setUp(savedInstanceState: Bundle?) {

    }

    abstract fun init(savedInstanceState: Bundle?)

    fun hasLocationPermission() =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED

    fun requestLocationPermission(requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION),
                requestCode)
    }

    fun showKeyboard(view: View) {
        view.post {
            view.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun hideKeyboard(view: View? = null) {
        view?.clearFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow((view ?: findViewById(android.R.id.content)).windowToken, 0)
    }
}