package com.eokoe.sagui.features.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.widgets.dialog.BoxDialog
import kotlinx.android.synthetic.main.dialog_category_actions.*

/**
 * @author Pedro Silva
 * @since 20/09/17
 */
class CategoryActionsDialog : BoxDialog() {
    override val closeButton: View?
        get() = btnClose
    override val boxView: View
        get() = rlActions

    private lateinit var category: Category
    private val onActionClickListener: OnActionClickListener?
        get() = activity as? OnActionClickListener

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_category_actions, parent, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        category = arguments.getParcelable(EXTRA_CATEGORY)
        tvCategoryName.text = category.name

        llAnswerSurvey.setOnClickListener {
            onActionClickListener?.onAnswerSurveyClick(category)
            dismiss()
        }
        llSeeComplaints.setOnClickListener {
            onActionClickListener?.onSeeComplaintsClick(category)
            dismiss()
        }
    }

    companion object {
        private val EXTRA_CATEGORY = "EXTRA_CATEGORY"

        fun newInstance(category: Category): CategoryActionsDialog {
            val fragment = CategoryActionsDialog()
            val args = Bundle()
            args.putParcelable(EXTRA_CATEGORY, category)
            fragment.arguments = args
            fragment.isCancelable = false
            return fragment
        }
    }

    interface OnActionClickListener {
        fun onAnswerSurveyClick(category: Category)
        fun onSeeComplaintsClick(category: Category)
    }
}