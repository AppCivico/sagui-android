package com.eokoe.sagui.widgets.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog

/**
 * @author Pedro Silva
 * @since 14/09/17
 */
class AlertDialogFragment : DialogLoadFragment<Any>() {
    val TAG = AlertDialogFragment::class.simpleName

    var onDismissListener: OnDismissListener? = null

    override fun show(manager: FragmentManager) {
        show(manager, TAG)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context)
                .setTitle(arguments.getString(EXTRA_TITLE))
                .setMessage(arguments.getString(EXTRA_MESSAGE))
                .setPositiveButton(android.R.string.ok, { dialog, which ->
                    dialog.dismiss()
                })
                .setCancelable(isCancelable)
                .create()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        onDismissListener?.onDismiss()
        super.onDismiss(dialog)
    }

    companion object {
        private val EXTRA_TITLE = "EXTRA_TITLE"
        private val EXTRA_MESSAGE = "EXTRA_MESSAGE"

        fun newInstance(context: Context, @StringRes title: Int, @StringRes message: Int,
                        cancelable: Boolean = false) =
                newInstance(context.getString(title), context.getString(message), cancelable)

        fun newInstance(title: String, message: String, cancelable: Boolean = false): AlertDialogFragment {
            val frag = AlertDialogFragment()
            val args = Bundle()
            args.putString(EXTRA_TITLE, title)
            args.putString(EXTRA_MESSAGE, message)
            frag.arguments = args
            frag.isCancelable = cancelable
            return frag
        }
    }

    interface OnDismissListener {
        fun onDismiss()
    }
}