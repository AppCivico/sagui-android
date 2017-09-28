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
class ConfirmDialogFragment : DialogLoadFragment<Any>() {
    val TAG = ConfirmDialogFragment::class.simpleName

    var onActionListener: OnActionClickListener? = null
    var onDismissListener: OnDismissListener? = null

    override fun show(manager: FragmentManager) {
        show(manager, TAG)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context)
                .setTitle(arguments.getString(EXTRA_TITLE))
                .setMessage(arguments.getString(EXTRA_MESSAGE))
                .setNegativeButton(arguments.getString(EXTRA_NEGATIVE_BUTTON), { dialog, which ->
                    onActionListener?.onCancel(dialog)
                })
                .setPositiveButton(arguments.getString(EXTRA_POSITIVE_BUTTON), { dialog, which ->
                    onActionListener?.onConfirm(dialog)
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
        private val EXTRA_POSITIVE_BUTTON = "EXTRA_POSITIVE_BUTTON"
        private val EXTRA_NEGATIVE_BUTTON = "EXTRA_NEGATIVE_BUTTON"

        inline fun newInstance(context: Context? = null, block: Builder.() -> Unit) = Builder(context).apply(block).build()
    }

    class Builder(val context: Context?) {
        var title: String? = null
            get() = field ?: context?.getString(titleRes)

        var message: String? = null
            get() = field ?: context?.getString(messageRes)

        var positiveText: String? = null
            get() = field ?: context?.getString(positiveTextRes)

        var negativeText: String? = null
            get() = field ?: context?.getString(negativeTextRes)

        @StringRes
        var titleRes: Int = 0
        @StringRes
        var messageRes: Int = 0
        @StringRes
        var positiveTextRes: Int = android.R.string.ok
        @StringRes
        var negativeTextRes: Int = android.R.string.cancel

        var cancelable: Boolean = false

        fun build(): ConfirmDialogFragment {
            val frag = ConfirmDialogFragment()
            val args = Bundle()
            args.putString(EXTRA_TITLE, title)
            args.putString(EXTRA_MESSAGE, message)
            args.putString(EXTRA_POSITIVE_BUTTON, positiveText)
            args.putString(EXTRA_NEGATIVE_BUTTON, negativeText)
            frag.arguments = args
            frag.isCancelable = cancelable
            return frag
        }
    }

    interface OnActionClickListener {
        fun onConfirm(dialog: DialogInterface)
        fun onCancel(dialog: DialogInterface)
    }

    interface OnDismissListener {
        fun onDismiss()
    }
}