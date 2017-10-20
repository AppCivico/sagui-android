package com.eokoe.sagui.widgets.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatCheckBox
import android.text.method.LinkMovementMethod
import android.view.ViewGroup
import com.eokoe.sagui.R
import kotlinx.android.synthetic.main.dialog_alert.view.*

/**
 * @author Pedro Silva
 * @since 14/09/17
 */
class AlertDialogFragment : DialogFragment() {
    private var onConfirmClickListener: DialogInterface.OnClickListener? = null
    private var onCancelClickListener: DialogInterface.OnClickListener? = null
    private var onDismissListener: DialogInterface.OnDismissListener? = null
    private var onMultiChoiceClickListener: DialogInterface.OnMultiChoiceClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertView = activity.layoutInflater.inflate(R.layout.dialog_alert, null) as ViewGroup
        alertView.message.text = arguments.getCharSequence(EXTRA_MESSAGE)

        if (arguments.getBoolean(EXTRA_HAS_LINK)) {
            alertView.message.movementMethod = LinkMovementMethod.getInstance()
        }

        val alertDialog = AlertDialog.Builder(context)
                .setView(alertView)
                .setTitle(arguments.getString(EXTRA_TITLE))
                .setCancelable(isCancelable)
                .setPositiveButton(arguments.getString(EXTRA_POSITIVE_BUTTON), onConfirmClickListener)

        val multiChoiceItems = arguments.getStringArray(EXTRA_MULTICHOICE_ITEMS)
        if (multiChoiceItems != null) {
            val checkedItems = arguments.getBooleanArray(EXTRA_MULTICHOICE_ITEMS_SELECTED)
            multiChoiceItems.forEachIndexed { index, str ->
                val checkbox = AppCompatCheckBox(context)
                checkbox.text = str
                checkbox.isChecked = checkedItems?.isNotEmpty() == true &&
                        checkedItems.size > index && checkedItems[index]
                checkbox.setOnCheckedChangeListener { _, checked ->
                    onMultiChoiceClickListener?.onClick(dialog, index, checked)
                }
                alertView.addView(checkbox)
            }
        }

        val negativeText = arguments.getString(EXTRA_NEGATIVE_BUTTON)
        if (negativeText != null) {
            alertDialog.setNegativeButton(negativeText, onCancelClickListener)
        }
        return alertDialog.create()
    }

    fun show(manager: FragmentManager) {
        show(manager, arguments.getString(EXTRA_TAG))
    }

    override fun onDestroyView() {
        if (dialog != null && retainInstance) {
            dialog.setDismissMessage(null)
        }
        super.onDestroyView()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        onDismissListener?.onDismiss(dialog)
        super.onDismiss(dialog)
    }

    companion object {
        private val EXTRA_TAG = "EXTRA_TAG"
        private val EXTRA_TITLE = "EXTRA_TITLE"
        private val EXTRA_MESSAGE = "EXTRA_MESSAGE"
        private val EXTRA_POSITIVE_BUTTON = "EXTRA_POSITIVE_BUTTON"
        private val EXTRA_NEGATIVE_BUTTON = "EXTRA_NEGATIVE_BUTTON"
        private val EXTRA_MULTICHOICE_ITEMS = "EXTRA_MULTICHOICE_ITEMS"
        private val EXTRA_MULTICHOICE_ITEMS_SELECTED = "EXTRA_MULTICHOICE_ITEMS_SELECTED"
        private val EXTRA_HAS_LINK = "EXTRA_HAS_LINK"

        fun create(context: Context, block: Builder.() -> Unit) = Builder(context).apply(block).build()
    }

    class Builder(val context: Context) {
        var tag: String? = AlertDialogFragment::class.simpleName

        var title: String? = null
            get() = field ?: getString(titleRes)
        var message: CharSequence? = null
            get() = field ?: getString(messageRes)

        var positiveText: String? = null
            get() = field ?: getString(positiveTextRes)
        var negativeText: String? = null
            get() = field ?: getString(negativeTextRes)
        var multiChoiceItems: Array<String>? = null
        var multiChoiceItemsSelected: BooleanArray? = null

        @StringRes
        var titleRes: Int = 0
        @StringRes
        var messageRes: Int = 0
        @StringRes
        var positiveTextRes: Int = R.string.ok
        @StringRes
        var negativeTextRes: Int = 0

        var cancelable: Boolean = false
        var hasLink: Boolean = false

        var onConfirmClickListener: DialogInterface.OnClickListener =
                DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() }
        var onCancelClickListener: DialogInterface.OnClickListener =
                DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() }

        var onDismissListener: DialogInterface.OnDismissListener? = null
        var onMultiChoiceClickListener: DialogInterface.OnMultiChoiceClickListener? = null

        fun build(): AlertDialogFragment {
            val frag = AlertDialogFragment()
            val args = Bundle()
            args.putString(EXTRA_TAG, tag)
            args.putString(EXTRA_TITLE, title)
            args.putCharSequence(EXTRA_MESSAGE, message)
            args.putString(EXTRA_POSITIVE_BUTTON, positiveText)
            args.putString(EXTRA_NEGATIVE_BUTTON, negativeText)
            args.putStringArray(EXTRA_MULTICHOICE_ITEMS, multiChoiceItems)
            args.putBooleanArray(EXTRA_MULTICHOICE_ITEMS_SELECTED, multiChoiceItemsSelected)
            args.putBoolean(EXTRA_HAS_LINK, hasLink)
            frag.arguments = args
            frag.isCancelable = cancelable
            frag.onConfirmClickListener = onConfirmClickListener
            frag.onCancelClickListener = onCancelClickListener
            frag.onDismissListener = onDismissListener
            frag.onMultiChoiceClickListener = onMultiChoiceClickListener
            return frag
        }

        inline fun onDismissListener(crossinline listener: (dialog: DialogInterface) -> Unit) {
            onDismissListener = DialogInterface.OnDismissListener { dialog ->
                listener(dialog)
            }
        }

        inline fun onConfirmClickListener(crossinline listener: (dialog: DialogInterface, which: Int) -> Unit) {
            onConfirmClickListener = DialogInterface.OnClickListener { dialog, which ->
                listener(dialog, which)
            }
        }

        inline fun onCancelClickListener(crossinline listener: (dialog: DialogInterface, which: Int) -> Unit) {
            onCancelClickListener = DialogInterface.OnClickListener { dialog, which ->
                listener(dialog, which)
            }
        }

        inline fun onMultiChoiceClickListener(
                items: Array<String>? = multiChoiceItems,
                selectedItems: BooleanArray? = null,
                crossinline listener: (dialog: DialogInterface, index: Int, isChecked: Boolean) -> Unit
        ) {
            multiChoiceItems = items
            multiChoiceItemsSelected = selectedItems
            onMultiChoiceClickListener = DialogInterface.OnMultiChoiceClickListener { dialog, index, isChecked ->
                listener(dialog, index, isChecked)
            }
        }

        private fun getString(@StringRes resId: Int) =
                if (resId > 0) context.getString(resId) else null
    }
}