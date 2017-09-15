package com.eokoe.sagui.features.surveys.survey

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.GridLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.*
import com.eokoe.sagui.data.model.impl.SurveyModelImpl
import com.eokoe.sagui.extensions.*
import com.eokoe.sagui.features.base.view.BaseActivity
import com.eokoe.sagui.features.base.view.ViewPresenter
import com.eokoe.sagui.features.surveys.survey.note.NoteActivity
import com.eokoe.sagui.utils.LocationHelper
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
        SurveyContract.View, ViewPresenter<SurveyContract.Presenter>, LocationHelper.OnLocationReceivedListener {

    private val REQUEST_CODE_START_QUESTIONS = 1
    private val REQUEST_GOOGLE_PLAY_RESOLVE_ERROR = 1001

    override lateinit var presenter: SurveyContract.Presenter
    private lateinit var progressDialog: LoadingDialog
    private var submissionsId: String? = null
    private val locationHelper = LocationHelper()
    private var location: LatLong? = null

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
        locationHelper.registerOnConnectionFailed(this, REQUEST_GOOGLE_PLAY_RESOLVE_ERROR)
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
            if (hasLocationPermission()) {
                requestLocation()
                presenter.start()
            } else {
                requestLocationPermission(REQUEST_CODE_START_QUESTIONS)
            }
        }
        btnNo.setOnClickListener {
            surveyAnswered()
        }
        btnYes.setOnClickListener {
            startActivityForResult(NoteActivity.getIntent(this@SurveyActivity, submissionsId!!), REQUEST_NOTES)
        }
    }

    override fun onStart() {
        super.onStart()
        locationHelper.start()
    }

    override fun onStop() {
        locationHelper.stop()
        super.onStop()
    }

    override fun onBackPressed() {
        if (!rlQuestionsBox.isVisible()) {
            super.onBackPressed()
        } else {
            hideQuestions()
        }
    }

    override fun showQuestion(question: Question) {
        if (!rlQuestionsBox.isVisible()) {
            backdrop.showAnimated()
            rlQuestionsBox.showSlidingTop()
            window.statusBarOverlay()
        }
        loadQuestion(question)
    }

    override fun updateProgress(index: Int, size: Int) {
        circleProgress.max = size
        circleProgress.progress = index.toFloat()

        horizontalProgress.max = size
        horizontalProgress.progress = index
    }

    override fun hideQuestions() {
        hideKeyboard()
        rlQuestionsBox.hideSlidingBottom()
        backdrop.hideAnimated()
        window.restoreStatusBarColor()
    }

    override fun finalize(answers: List<Answer>) {
        hideQuestions()
        presenter.sendAnswers(answers, location)
    }

    override fun answersSent(submissions: Submissions) {
        actionsStart.hide()
        actionsFinal.show()
        submissionsId = submissions.id
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

    fun requestLocation() {
        if (hasLocationPermission()) {
            locationHelper.requestLocation(this, this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_NOTES) {
            if (resultCode == RESULT_OK) {
                surveyAnswered()
            }
            return
        } else if (requestCode == REQUEST_GOOGLE_PLAY_RESOLVE_ERROR) {
            locationHelper.onActivityResult(resultCode, data)
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_START_QUESTIONS) {
            requestLocation()
            presenter.start()
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun surveyAnswered() {
        Toast.makeText(this, "Enquete respondida", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun loadQuestion(question: Question) {
        tvSurveyTitle.text = question.name
        btnNext.disable()
        when (question.type) {
            Question.Type.TEXT -> {
                val answerText = findViewById<TextInputLayout?>(R.id.answerText)
                if (answerText == null) {
                    rlAnswer.removeAllViews()
                    buildViewText(question)
                } else {
                    answerText.editText!!.text = null
                }
            }
            Question.Type.MULTIPLE -> {
                rlAnswer.removeAllViews()
                buildViewMultiple(question)
            }
            Question.Type.TRAFFIC_LIGHT -> {
                rlAnswer.removeAllViews()
                buildViewTrafficLight(question)
            }
        }
    }

    private fun buildViewText(question: Question) {
        val viewAnswer = layoutInflater.inflate(R.layout.answer_text, rlAnswer, false) as TextInputLayout
        RxTextView.textChangeEvents(viewAnswer.editText!!)
                .subscribe {
                    btnNext.isEnabled = it.text().isNotEmpty()
                }
        rlAnswer.addView(viewAnswer)
        showKeyboard(viewAnswer.editText!!)
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
            val check = viewAnswer.findViewById<CheckableCircleImageView>(resId)
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

    override fun onLocationReceived(location: Location) {
        this.location = LatLong(location.latitude, location.longitude)
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