package com.eokoe.sagui.features.surveys.survey.note

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Comment
import com.eokoe.sagui.data.model.impl.SaguiModelImpl
import com.eokoe.sagui.extensions.ErrorType
import com.eokoe.sagui.extensions.errorType
import com.eokoe.sagui.features.base.view.BaseActivity
import com.eokoe.sagui.features.base.view.ViewPresenter
import com.eokoe.sagui.widgets.dialog.AlertDialogFragment
import com.eokoe.sagui.widgets.dialog.LoadingDialog
import kotlinx.android.synthetic.main.activity_note.*

/**
 * @author Pedro Silva
 * @since 30/08/17
 */
class NoteActivity : BaseActivity(), NoteContract.View, ViewPresenter<NoteContract.Presenter> {

    override lateinit var presenter: NoteContract.Presenter
    private lateinit var progressDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        setSupportActionBar(toolbar)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        super.setUp(savedInstanceState)
        showBackButton()
        presenter = NotePresenter(SaguiModelImpl())
        progressDialog = LoadingDialog.newInstance(getString(R.string.sending_note))
    }

    override fun init(savedInstanceState: Bundle?) {
        val surveyId = intent.extras.getString(EXTRA_SUBMISSIONS_ID)
        btnSend.setOnClickListener {
            if (textNote.editText!!.text.isNotEmpty()) {
                hideKeyboard(textNote.editText!!)
                presenter.sendNote(Comment(submissionsId = surveyId, content = textNote.editText!!.text.toString()))
            } else {
//                Toast.makeText(this, "Informe as observações", Toast.LENGTH_SHORT).show()
                AlertDialogFragment
                        .create(this) {
                            title = "Atenção"
                            message = "Informe as observações"
                        }
                        .show(supportFragmentManager)
            }
        }
    }

    override fun noteSent() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun showLoading() {
        progressDialog.show(supportFragmentManager)
    }

    override fun hideLoading() {
        progressDialog.dismiss()
    }

    override fun showError(error: Throwable) {
        hideLoading()
        AlertDialogFragment
                .create(this) {
                    title = "Falha ao enviar observações"
                    message = if (error.errorType == ErrorType.CONNECTION)
                        "Por favor verifique sua internet e tente novamente"
                    else "Ocorreu um erro inexperado.\nTente novamente mais tarde"
                }
                .show(supportFragmentManager)
    }

    companion object {
        private val EXTRA_SUBMISSIONS_ID = "EXTRA_SUBMISSIONS_ID"

        fun getIntent(context: Context, submissionsId: String): Intent {
            val intent = Intent(context, NoteActivity::class.java)
            intent.putExtra(EXTRA_SUBMISSIONS_ID, submissionsId)
            return intent
        }
    }
}