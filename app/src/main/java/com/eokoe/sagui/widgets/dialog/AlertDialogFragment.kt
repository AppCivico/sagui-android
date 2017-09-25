package com.eokoe.sagui.widgets.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.view.View

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
                .setTitle(arguments.getInt(EXTRA_TITLE))
                .setMessage(arguments.getInt(EXTRA_MESSAGE))
                .setPositiveButton(android.R.string.ok, { dialog, which ->
                    dialog.dismiss()
                })
                .setCancelable(isCancelable)
                .create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        if (onDismissListener != null) {
            onDismissListener?.onDismiss()
        }
        super.onDismiss(dialog)
    }

    companion object {
        private val EXTRA_TITLE = "EXTRA_TITLE"
        private val EXTRA_MESSAGE = "EXTRA_MESSAGE"

        fun newInstance(@StringRes title: Int, @StringRes message: Int,
                        cancelable: Boolean = false): AlertDialogFragment {
            val frag = AlertDialogFragment()
            val args = Bundle()
            args.putInt(EXTRA_TITLE, title)
            args.putInt(EXTRA_MESSAGE, message)
            frag.arguments = args
            frag.isCancelable = cancelable
            return frag
        }
    }

    interface OnDismissListener {
        fun onDismiss()
    }
}