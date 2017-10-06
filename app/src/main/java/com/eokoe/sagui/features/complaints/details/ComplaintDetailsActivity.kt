package com.eokoe.sagui.features.complaints.details

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Complaint
import com.eokoe.sagui.data.entities.Confirmation
import com.eokoe.sagui.data.model.impl.SaguiModelImpl
import com.eokoe.sagui.extensions.ErrorType
import com.eokoe.sagui.extensions.errorType
import com.eokoe.sagui.extensions.friendlyMessage
import com.eokoe.sagui.features.base.view.BaseActivity
import com.eokoe.sagui.features.base.view.ViewPresenter
import com.eokoe.sagui.widgets.dialog.AlertDialogFragment
import com.eokoe.sagui.widgets.dialog.LoadingDialog
import kotlinx.android.synthetic.main.activity_complaint_details.*

/**
 * @author Pedro Silva
 * @since 28/09/17
 */
class ComplaintDetailsActivity : BaseActivity(),
        ConfirmContract.View, ViewPresenter<ConfirmContract.Presenter> {

    override lateinit var presenter: ConfirmContract.Presenter
    private lateinit var complaint: Complaint
    private lateinit var loadingDialog: LoadingDialog
    private var isConfirmed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complaint_details)
    }

    override fun onBackPressed() {
        if (isConfirmed) {
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    override fun setUp(savedInstanceState: Bundle?) {
        showBackButton()
        complaint = intent.extras.getParcelable(EXTRA_COMPLAINT)
        presenter = ConfirmPresenter(SaguiModelImpl())
        loadingDialog = LoadingDialog.newInstance(getString(R.string.loading_confirm_complaint))
        btnConfirm.setOnClickListener {
            getConfirmDialog().show(supportFragmentManager)
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        rvComplaintDetails.setHasFixedSize(true)
        rvComplaintDetails.adapter = ComplaintDetailsAdapter(complaint)
    }

    override fun onComplaintConfirmed(confirmation: Confirmation) {
        isConfirmed = true
        getContributeDialog().show(supportFragmentManager)
    }

    override fun showLoading() {
        loadingDialog.show(supportFragmentManager)
    }

    override fun hideLoading() {
        loadingDialog.dismiss()
    }

    override fun showError(error: Throwable) {
        hideLoading()
        getErrorDialog(error).show(supportFragmentManager)
    }

    private fun getConfirmDialog(): AlertDialogFragment {
        return AlertDialogFragment.create(this) {
            titleRes = R.string.confirm_complaint
            messageRes = R.string.confirm_complaint_question
            positiveTextRes = R.string.confirm
            negativeTextRes = R.string.cancel
            cancelable = true
            onConfirmClickListener { dialog, which ->
                presenter.confirmComplaint(complaint)
            }
        }
    }

    private fun getContributeDialog(): AlertDialogFragment {
        // TODO change to contribute
        return AlertDialogFragment.create(this) {
            titleRes = R.string.confirmed
//            messageRes = R.string.msg_contribute
            message = "Sua confirmação foi registrada."
//            positiveTextRes = R.string.contribute
//            negativeTextRes = R.string.cancel
            cancelable = true
            onConfirmClickListener { dialog, which ->
                dialog.dismiss()
            }
        }
    }

    private fun getErrorDialog(error: Throwable): AlertDialogFragment {
        return AlertDialogFragment.create(this) {
            titleRes = R.string.error
            message = when (error.errorType) {
                ErrorType.CONNECTION -> "Error de conexão.\nPor favor verifique sua internet e tente novamente"
                ErrorType.CUSTOM -> error.friendlyMessage
                else -> "Ocorreu um erro inexperado.\nTente novamente mais tarde"
            }
        }
    }

    companion object {
        private val EXTRA_COMPLAINT = "EXTRA_COMPLAINT"

        fun getIntent(context: Context, complaint: Complaint): Intent =
                Intent(context, ComplaintDetailsActivity::class.java)
                        .putExtra(EXTRA_COMPLAINT, complaint)
    }
}