package com.eokoe.sagui.features.complaints.details

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Complaint
import com.eokoe.sagui.features.base.view.BaseActivity
import com.eokoe.sagui.widgets.dialog.ConfirmDialogFragment
import kotlinx.android.synthetic.main.activity_complaint_details.*

/**
 * @author Pedro Silva
 * @since 28/09/17
 */
class ComplaintDetailsActivity : BaseActivity() {

    private lateinit var complaint: Complaint

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complaint_details)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        showBackButton()
        complaint = intent.extras.getParcelable(EXTRA_COMPLAINT)
        tvTitle.text = complaint.title
        tvCategoryName.text = complaint.category?.name
        tvLocation.text = complaint.address
        tvQtyComplaints.text = resources.getQuantityString(
                R.plurals.qty_confirmations, complaint.confirmations, complaint.confirmations)
        val remain = MAX_COUNT_CONFIRMATION - complaint.confirmations
        if (remain > 0) {
            tvQtyRemain.text = resources.getQuantityString(R.plurals.qty_remain, remain, remain)
        } else {
            tvQtyRemain.setText(R.string.occurrence_already)
        }
        tvDescription.text = complaint.description
        btnConfirm.setOnClickListener {
            getConfirmDialog().show(supportFragmentManager)
        }
    }

    private fun getConfirmDialog(): ConfirmDialogFragment {
        val dialog = ConfirmDialogFragment.newInstance(this) {
            titleRes = R.string.confirm_complaint
            messageRes = R.string.confirm_complaint_question
            positiveTextRes = R.string.confirm
            cancelable = true
        }
        dialog.onActionListener = object : ConfirmDialogFragment.OnActionClickListener {
            override fun onCancel(dialog: DialogInterface) {
                dialog.dismiss()
            }

            override fun onConfirm(dialog: DialogInterface) {
                dialog.dismiss()
            }
        }
        return dialog
    }

    override fun init(savedInstanceState: Bundle?) {

    }

    companion object {
        private val EXTRA_COMPLAINT = "EXTRA_COMPLAINT"
        private val MAX_COUNT_CONFIRMATION = 30

        fun getIntent(context: Context, complaint: Complaint): Intent {
            val intent = Intent(context, ComplaintDetailsActivity::class.java)
            intent.putExtra(EXTRA_COMPLAINT, complaint)
            return intent
        }
    }
}