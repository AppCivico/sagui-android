package com.eokoe.sagui.widgets.dialog

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager


/**
 * @author Pedro Silva
 * @since 14/09/17
 */
abstract class DialogLoadFragment<T> : DialogFragment() {

    interface OnResultListener<in R> {
        fun onResult(result: R)
    }

    protected var mListener: OnResultListener<T>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    fun setOnResultListener(listener: OnResultListener<T>) {
        this.mListener = listener
    }

    override fun onDestroyView() {
        if (dialog != null && retainInstance) {
            dialog.setDismissMessage(null)
        }
        super.onDestroyView()
    }

    abstract fun show(manager: FragmentManager)
}