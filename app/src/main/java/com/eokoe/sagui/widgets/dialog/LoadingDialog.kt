package com.eokoe.sagui.widgets.dialog

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.view.View


/**
 * @author Pedro Silva
 * @since 14/09/17
 */
class LoadingDialog : DialogLoadFragment<Any>() {
    val TAG = "LOAD_DIALOG"

    override fun show(manager: FragmentManager) {
        show(manager, TAG)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = ProgressDialog(activity)
        dialog.setCancelable(false)
        dialog.setMessage(arguments.getString(EXTRA_LOADING_MESSAGE))
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        private val EXTRA_LOADING_MESSAGE = "EXTRA_LOADING_MESSAGE"

        fun newInstance(msg: String): LoadingDialog {
            val frag = LoadingDialog()
            val args = Bundle()
            args.putString(EXTRA_LOADING_MESSAGE, msg)
            frag.arguments = args
            return frag
        }
    }
}