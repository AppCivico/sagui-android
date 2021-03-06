package com.eokoe.sagui.features.base.view

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.eokoe.sagui.extensions.releaseContext
import com.eokoe.sagui.features.base.presenter.BasePresenter
import com.eokoe.sagui.utils.LocationHelper
import com.eokoe.sagui.utils.LogUtil
import com.eokoe.sagui.widgets.dialog.AlertDialogFragment


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

    override fun onResume() {
        super.onResume()
        (this as? ViewLocation)?.locationHelper?.resume()
    }

    override fun onStart() {
        super.onStart()
        (this as? ViewLocation)?.locationHelper?.start(this)
    }

    override fun onPause() {
        releaseContext()
        super.onPause()
    }

    override fun onStop() {
        (this as? ViewLocation)?.locationHelper?.stop()
        super.onStop()
    }

    override fun onDestroy() {
        if (this is ViewPresenter<*>) {
            presenter.detach()
        }
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == LocationHelper.REQUEST_GOOGLE_PLAY_RESOLVE_ERROR && this is ViewLocation) {
            locationHelper.onActivityResult(resultCode)
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun startActivityAndClearStack(intent: Intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    open fun showBackButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    open fun setUp(savedInstanceState: Bundle?) {}

    abstract fun init(savedInstanceState: Bundle?)

    fun hasLocationPermission() = hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)

    fun hasCameraPermission() = hasPermission(Manifest.permission.CAMERA)

    fun hasRecordAudioPermission() = hasPermission(Manifest.permission.RECORD_AUDIO)

    fun hasPermission(permission: String) =
            ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    fun requestLocationPermission(@StringRes title: Int, @StringRes message: Int, requestCode: Int) {
        requestPermission(title, message, requestCode,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)
    }

    fun requestCameraPermission(requestCode: Int) {
        // TODO handle permission not granted
        if (!hasCameraPermission()) {
            requestPermission(requestCode, Manifest.permission.CAMERA)
        }
    }

    fun requestRecordAudioPermission(requestCode: Int) {
        // TODO handle permission not granted
        if (!hasRecordAudioPermission()) {
            requestPermission(requestCode, Manifest.permission.RECORD_AUDIO)
        }
    }

    fun requestPermission(@StringRes title: Int, @StringRes message: Int,
                          requestCode: Int, vararg permissions: String) {
        val alertDialog = AlertDialogFragment.create(this) {
            titleRes = title
            messageRes = message
            onConfirmClickListener { _, _ ->
                ActivityCompat.requestPermissions(this@BaseActivity, permissions, requestCode)
            }
        }
        alertDialog.show(supportFragmentManager)
    }

    fun requestPermission(requestCode: Int, vararg permissions: String) {
        ActivityCompat.requestPermissions(this@BaseActivity, permissions, requestCode)
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

    fun getAlertList(list: Array<String>, listener: (DialogInterface, Int) -> Unit): AlertDialog {
        return AlertDialog.Builder(this)
                .setItems(list, listener)
                .create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveInstanceState(outState)
    }

    abstract fun saveInstanceState(outState: Bundle)

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState)
        }
    }

    abstract fun restoreInstanceState(savedInstanceState: Bundle)

    fun grantUriRwPermissions(intent: Intent, file: Uri?) {
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        val resInfoList = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        resInfoList
                .map { it.activityInfo.packageName }
                .forEach {
                    grantUriPermission(it, file,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
    }
}