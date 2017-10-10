package com.eokoe.sagui.features.surveys.survey

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Animatable
import android.location.Location
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.GridLayout
import android.util.Log
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.*
import com.eokoe.sagui.data.model.impl.SaguiModelImpl
import com.eokoe.sagui.extensions.*
import com.eokoe.sagui.features.base.view.BaseActivity
import com.eokoe.sagui.features.base.view.ViewLocation
import com.eokoe.sagui.features.base.view.ViewPresenter
import com.eokoe.sagui.features.surveys.survey.note.NoteActivity
import com.eokoe.sagui.utils.LocationHelper
import com.eokoe.sagui.widgets.CheckableImageView
import com.eokoe.sagui.widgets.dialog.AlertDialogFragment
import com.eokoe.sagui.widgets.dialog.LoadingDialog
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.ImageRequest
import com.jakewharton.rxbinding2.widget.RxTextView
import kotlinx.android.synthetic.main.activity_questions.*
import kotlinx.android.synthetic.main.content_questions.*


/**
 * @author Pedro Silva
 * @since 16/08/17
 */
class SurveyActivity : BaseActivity(),
        SurveyContract.View, ViewPresenter<SurveyContract.Presenter>,
        ViewLocation, LocationHelper.OnLocationReceivedListener {

    override lateinit var presenter: SurveyContract.Presenter
    private lateinit var progressDialog: LoadingDialog
    private var submissionsId: String? = null
    private var location: LatLong? = null
    override var locationHelper = LocationHelper()

    var questionBoxOpened: Boolean = false
    override var currentProgress: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)
        setSupportActionBar(toolbar)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        super.setUp(savedInstanceState)
        showBackButton()
        presenter = SurveyPresenter(SaguiModelImpl())
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
            if (requestLocation()) {
                presenter.start()
            } else {
                requestLocationPermission(R.string.title_request_location_permission,
                        R.string.message_request_location_permission, REQUEST_PERMISSION_LOCATION)
            }
        }
        btnNo.setOnClickListener {
            surveyAnswered()
        }
        btnYes.setOnClickListener {
            startActivityForResult(NoteActivity.getIntent(this@SurveyActivity, submissionsId!!), REQUEST_CODE_NOTES)
        }
        if (questionBoxOpened) {
            btnStart.performClick()
        }
    }

    override fun onBackPressed() {
        if (!rlQuestionsBox.isVisible) {
            super.onBackPressed()
        } else {
            presenter.back()
        }
    }

    override fun showQuestion(question: Question, answer: Answer?) {
        questionBoxOpened = true
        if (!rlQuestionsBox.isVisible) {
            backdrop.showAnimated()
            rlQuestionsBox.showSlidingTop()
            window.statusBarOverlay()
        }
        loadQuestion(question, answer)
    }

    override fun updateProgress(index: Int, size: Int) {
        currentProgress = index
        circleProgress.max = size
        circleProgress.progress = index.toFloat()

        horizontalProgress.max = size
        horizontalProgress.progress = index

        if (index < size - 1) {
            btnNext.text = getString(R.string.next)
        } else {
            btnNext.text = getString(R.string.send)
        }
    }

    override fun hideQuestions() {
        questionBoxOpened = false
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
        AlertDialogFragment
                .create(this) {
                    title = "Falha ao enviar respostas"
                    message = if (error.errorType == ErrorType.CONNECTION)
                        "Por favor verifique sua internet e tente novamente"
                    else "Ocorreu um erro inexperado.\nTente novamente mais tarde"
                }
                .show(supportFragmentManager)
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation(): Boolean {
        if (hasLocationPermission()) {
            locationHelper.requestLocation(this, this)
            return true
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_NOTES) {
            if (resultCode == RESULT_OK) {
                surveyAnswered()
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            requestLocation()
            presenter.start()
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun surveyAnswered() {
        setResult(RESULT_OK)
        finish()
    }

    private fun loadQuestion(question: Question, answer: Answer?) {
        tvSurveyTitle.text = question.name
        btnNext.disable()
        if (question.type != Question.Type.TEXT) {
            hideKeyboard()
            rlAnswer.removeAllViews()
        }
        when (question.type) {
            Question.Type.TEXT -> {
                buildViewText(question, answer)
            }
            Question.Type.MULTIPLE -> {
                buildViewMultiple(question, answer)
            }
            Question.Type.TRAFFIC_LIGHT -> {
                buildViewTrafficLight(question, answer)
            }
        }
    }

    private fun buildViewText(question: Question, answer: Answer?) {
        var viewAnswer = findViewById<TextInputLayout?>(R.id.answerText)
        if (viewAnswer == null) {
            rlAnswer.removeAllViews()
            viewAnswer = layoutInflater.inflate(R.layout.answer_text, rlAnswer, false) as TextInputLayout
            RxTextView.textChangeEvents(viewAnswer.editText!!)
                    .subscribe {
                        btnNext.isEnabled = it.text().isNotEmpty()
                    }
            viewAnswer.editText?.setOnEditorActionListener { v, actionId, event ->
                if (btnNext.isEnabled && (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE)) {
                    btnNext.performClick()
                    true
                } else {
                    false
                }
            }
            rlAnswer.addView(viewAnswer)
        }
        viewAnswer.editText!!.setText(answer?.value)
        viewAnswer.editText!!.setSelection(answer?.value?.length ?: 0)
        showKeyboard(viewAnswer.editText!!)
        btnNext.setOnClickListener {
            presenter.answer(question.id, viewAnswer!!.editText?.text.toString())
        }
    }

    private fun buildViewMultiple(question: Question, answered: Answer?) {
        val viewAnswer = layoutInflater.inflate(R.layout.answer_multiple, rlAnswer, false) as GridLayout
        var answerSelected: Answer? = null
        var answerSelectedView: RadioButton? = null
        question.answers?.forEach { answer ->
            val view = layoutInflater.inflate(R.layout.answer_multiple_radio, viewAnswer, false) as RadioButton
            view.text = answer.value
            view.setOnCheckedChangeListener { button, checked ->
                if (checked && button != answerSelectedView) {
                    answerSelectedView?.isChecked = false
                    answerSelectedView = button as RadioButton
                    answerSelected = answer
                    btnNext.enable()
                }
            }
            view.isChecked = answer.value == answered?.value
            viewAnswer.addView(view)
        }
        rlAnswer.addView(viewAnswer)
        btnNext.setOnClickListener {
            presenter.answer(question.id, answerSelected!!)
        }
    }

    private fun buildViewTrafficLight(question: Question, answered: Answer?) {
        val viewAnswer = layoutInflater.inflate(R.layout.answer_traffic_light, rlAnswer, false) as RadioGroup
        var answerSelected: Answer? = null
        rlAnswer.addView(viewAnswer)
        question.answers?.forEach { answer ->
            val resId = resources.getIdentifier(answer.unit?.name?.toLowerCase(), "id", packageName)
            val check = viewAnswer.findViewById<CheckableImageView>(resId)
            if (answer.image != null) {
                check.controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(ImageRequest.fromUri(answer.image))
                        .setControllerListener(MyControllerListener(check))
                        .build()
            }
            ((check.parent as ViewGroup).getChildAt(1) as TextView).text = answer.value
            check.setOnCheckedChangeListener(object : CheckableImageView.OnCheckedChangeListener {
                override fun onCheckedChanged(view: CheckableImageView, checked: Boolean) {
                    if (checked) {
                        answerSelected = answer
                        btnNext.enable()
                    }
                }
            })
            check.isChecked = answer.unit == answered?.unit
        }
        btnNext.setOnClickListener {
            presenter.answer(question.id, answerSelected!!)
        }
    }

    override fun onLocationReceived(location: Location) {
        this.location = LatLong(location.latitude, location.longitude)
    }

    override fun saveInstanceState(outState: Bundle) {
        outState.putInt(STATE_CURRENT_PROGRESS, currentProgress)
        outState.putBoolean(STATE_QUESTION_BOX_OPENED, questionBoxOpened)
    }

    override fun restoreInstanceState(savedInstanceState: Bundle) {
        currentProgress = savedInstanceState.getInt(STATE_CURRENT_PROGRESS)
        questionBoxOpened = savedInstanceState.getBoolean(STATE_QUESTION_BOX_OPENED)
    }

    companion object {
        private val EXTRA_CATEGORY = "EXTRA_CATEGORY"
        private val EXTRA_SURVEY = "EXTRA_SURVEY"
        private val STATE_CURRENT_PROGRESS = "STATE_CURRENT_PROGRESS"
        private val STATE_QUESTION_BOX_OPENED = "STATE_QUESTION_BOX_OPENED"
        private val REQUEST_CODE_NOTES = 1
        private val REQUEST_PERMISSION_LOCATION = 2

        fun getIntent(context: Context, category: Category, survey: Survey): Intent {
            val intent = Intent(context, SurveyActivity::class.java)
            intent.putExtra(EXTRA_CATEGORY, category)
            intent.putExtra(EXTRA_SURVEY, survey)
            return intent
        }
    }

    inner class MyControllerListener(private val image: SimpleDraweeView) : BaseControllerListener<ImageInfo>() {
        override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
            image.setBackgroundResource(R.drawable.bg_check_traffic_light_square)
        }

        override fun onFailure(id: String, throwable: Throwable) {
            Log.i("DraweeUpdate", "Image failed to load: " + throwable.message)
        }
    }
}