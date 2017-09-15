package com.eokoe.sagui.features.surveys.survey.note

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Comment
import com.eokoe.sagui.data.model.impl.SurveyModelImpl
import com.eokoe.sagui.features.base.view.BaseActivity
import com.eokoe.sagui.features.base.view.ViewPresenter
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
        presenter = NotePresenter(SurveyModelImpl())
        progressDialog = LoadingDialog.newInstance(getString(R.string.sending_note))
    }

    override fun init(savedInstanceState: Bundle?) {
        val surveyId = intent.extras.getString(EXTRA_SURVEY_ID)
        btnSend.setOnClickListener {
            presenter.sendNote(Comment(surveyId = surveyId, content = tvNote.text.toString()))
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
        Toast.makeText(this, "Falha ao enviar observações. Tente novamente", Toast.LENGTH_LONG).show()
    }

    companion object {
        private val EXTRA_SURVEY_ID = "EXTRA_SURVEY_ID"

        fun getIntent(context: Context, surveyId: String): Intent {
            val intent = Intent(context, NoteActivity::class.java)
            intent.putExtra(EXTRA_SURVEY_ID, surveyId)
            return intent
        }
    }
}