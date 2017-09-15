package com.eokoe.sagui.features.surveys.survey

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.GridLayout
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Answer
import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Question
import com.eokoe.sagui.data.entities.Survey
import com.eokoe.sagui.data.model.impl.SurveyModelImpl
import com.eokoe.sagui.extensions.*
import com.eokoe.sagui.features.base.view.BaseActivity
import com.eokoe.sagui.features.base.view.ViewPresenter
import com.eokoe.sagui.features.surveys.survey.note.NoteActivity
import com.eokoe.sagui.widgets.CheckableCircleImageView
import com.eokoe.sagui.widgets.dialog.LoadingDialog
import com.jakewharton.rxbinding2.widget.RxTextView
import kotlinx.android.synthetic.main.activity_questions.*
import kotlinx.android.synthetic.main.content_questions.*


/**
 * @author Pedro Silva
 * @since 16/08/17
 */
class SurveyActivity : BaseActivity(),
        SurveyContract.View, ViewPresenter<SurveyContract.Presenter> {

    override lateinit var presenter: SurveyContract.Presenter

    private lateinit var progressDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)
        setSupportActionBar(toolbar)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        super.setUp(savedInstanceState)
        showBackButton()
        presenter = SurveyPresenter(SurveyModelImpl())
        progressDialog = LoadingDialog.newInstance(getString(R.string.sending_answers))
    }

    @Suppress("UNCHECKED_CAST")
    override fun init(savedInstanceState: Bundle?) {
        val category: Category = intent.extras.getParcelable(EXTRA_CATEGORY)
        title = getString(R.string.title_activity_survey, category.name.toLowerCase())

        val survey: Survey = intent.extras.getParcelable(EXTRA_SURVEY)
        presenter.setSurvey(survey)

        btnClose.setOnClickListener {
            hideQuestions()
        }
        btnStart.setOnClickListener {
            presenter.start()
        }
        btnNo.setOnClickListener {
            surveyAnswered()
        }
        btnYes.setOnClickListener {
            startActivityForResult(NoteActivity.getIntent(this@SurveyActivity, survey.id), REQUEST_NOTES)
        }
    }

    override fun onBackPressed() {
        if (!rlQuestionsBox.isVisible()) {
            super.onBackPressed()
        } else {
            hideQuestions()
        }
    }

    override fun showQuestion(question: Question) {
        loadQuestion(question)
        backdrop.showAnimated()
        rlQuestionsBox.showSlidingTop()
        window.statusBarOverlay()
    }

    override fun loadQuestion(question: Question) {
        tvSurveyTitle.text = question.name
        rlAnswer.removeAllViews()
        btnNext.disable()
        when (question.type) {
            Question.Type.TEXT -> {
                buildViewText(question)
            }
            Question.Type.MULTIPLE -> {
                buildViewMultiple(question)
            }
            Question.Type.TRAFFIC_LIGHT -> {
                buildViewTrafficLight(question)
            }
        }
    }

    private fun buildViewText(question: Question) {
        val viewAnswer = layoutInflater.inflate(R.layout.answer_text, rlAnswer, false) as TextInputLayout
        rlAnswer.addView(viewAnswer)
        RxTextView.textChangeEvents(viewAnswer.editText!!)
                .subscribe {
                    btnNext.isEnabled = it.text().isNotEmpty()
                }
        viewAnswer.editText?.setOnEditorActionListener { v, actionId, event ->
            if (btnNext.isEnabled && (event?.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE)) {
                btnNext.performClick()
                true
            } else {
                false
            }
        }
        btnNext.setOnClickListener {
            presenter.answer(question.id, viewAnswer.editText?.text.toString())
        }
    }

    private fun buildViewMultiple(question: Question) {
        val viewAnswer = layoutInflater.inflate(R.layout.answer_multiple, rlAnswer, false) as GridLayout
        var answerSelected: Answer? = null
        var answerSelectedView: RadioButton? = null
        question.answers?.forEach { answer ->
            val view = layoutInflater.inflate(R.layout.answer_multiple_radio, viewAnswer, false) as RadioButton
            view.text = answer.value
            view.setOnClickListener {
                if (it != answerSelectedView) {
                    if (answerSelectedView != null)
                        answerSelectedView!!.isChecked = false
                    answerSelected = answer
                    answerSelectedView = it as RadioButton
                    btnNext.enable()
                }
            }
            viewAnswer.addView(view)
        }
        rlAnswer.addView(viewAnswer)
        btnNext.setOnClickListener {
            presenter.answer(question.id, answerSelected!!)
        }
    }

    private fun buildViewTrafficLight(question: Question) {
        val viewAnswer = layoutInflater.inflate(R.layout.answer_traffic_light, rlAnswer, false) as RadioGroup
        var answerSelected: Answer? = null
        question.answers?.forEach { answer ->
            val resId = resources.getIdentifier(answer.unit?.name?.toLowerCase(), "id", packageName)
            val check = viewAnswer.findViewById(resId) as CheckableCircleImageView
            check.setEnableText(false)
            check.setText(answer.value)
            check.setOnCheckedChangeListener(object : CheckableCircleImageView.OnCheckedChangeListener {
                override fun onCheckedChanged(view: CheckableCircleImageView, checked: Boolean) {
                    view.setEnableText(checked)
                    if (checked) answerSelected = answer
                    btnNext.enable()
                }
            })
        }
        rlAnswer.addView(viewAnswer)
        btnNext.setOnClickListener {
            presenter.answer(question.id, answerSelected!!)
        }
    }

    override fun updateProgress(index: Int, size: Int) {
        circleProgress.max = size
        circleProgress.progress = index.toFloat()

        horizontalProgress.max = size
        horizontalProgress.progress = index
    }

    override fun hideQuestions() {
        rlQuestionsBox.hideSlidingBottom()
        backdrop.hideAnimated()
        window.restoreStatusBarColor()
    }

    override fun finalize(answers: List<Answer>) {
        hideQuestions()
        // TODO send location
        presenter.sendAnswers(answers, null)
    }

    override fun answersSent() {
        actionsStart.hide()
        actionsFinal.show()
    }

    override fun showLoading() {
        progressDialog.show(supportFragmentManager)
    }

    override fun hideLoading() {
        progressDialog.dismiss()
    }

    override fun showError(error: Throwable) {
        hideLoading()
        btnStart.setText(R.string.send_again)
        Toast.makeText(this, "Falha ao enviar respostas. Tente novamente", Toast.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_NOTES) {
            if (resultCode == RESULT_OK) {
                surveyAnswered()
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun surveyAnswered() {
        Toast.makeText(this, "Enquete respondida", Toast.LENGTH_SHORT).show()
        finish()
    }

    companion object {
        private val EXTRA_CATEGORY = "EXTRA_CATEGORY"
        private val EXTRA_SURVEY = "EXTRA_SURVEY"
        private val REQUEST_NOTES = 1

        fun getIntent(context: Context, category: Category, survey: Survey): Intent {
            val intent = Intent(context, SurveyActivity::class.java)
            intent.putExtra(EXTRA_CATEGORY, category)
            intent.putExtra(EXTRA_SURVEY, survey)
            return intent
        }
    }
}