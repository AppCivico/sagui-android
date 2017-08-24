package com.eokoe.sagui.features.survey

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Question
import com.eokoe.sagui.data.entities.Survey
import com.eokoe.sagui.extensions.*
import com.eokoe.sagui.features.base.view.BaseActivity
import com.eokoe.sagui.features.base.view.ViewPresenter
import kotlinx.android.synthetic.main.activity_questions.*
import kotlinx.android.synthetic.main.content_questions.*

/**
 * @author Pedro Silva
 * @since 16/08/17
 */
class SurveyActivity : BaseActivity(),
        SurveyContract.View, ViewPresenter<SurveyContract.Presenter> {

    override lateinit var presenter: SurveyContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        super.setUp(savedInstanceState)
        presenter = SurveyPresenter()
    }

    @Suppress("UNCHECKED_CAST")
    override fun init(savedInstanceState: Bundle?) {
        val category = intent.extras[EXTRA_CATEGORY] as Category
        title = getString(R.string.title_activity_survey, category.title.toLowerCase())

        /*if (Build.VERSION.SDK_INT >= 23) {
            val decor = window.decorView
            decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }*/

        val survey = intent.extras[EXTRA_SURVEY] as Survey
        presenter.setSurvey(survey)

        btnClose.setOnClickListener {
            hideQuestions()
        }

        btnStart.setOnClickListener {
            presenter.start()
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
    }

    override fun loadQuestion(question: Question) {
        tvSurveyTitle.text = question.title
        tvQuestion.text = question.description
        btnNext.setOnClickListener {
            presenter.answer("")
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
    }

    override fun finalize() {
        actionsStart.hide()
        actionsFinal.show()
        hideQuestions()
    }

    companion object {
        private val EXTRA_CATEGORY = "EXTRA_CATEGORY"
        private val EXTRA_SURVEY = "EXTRA_SURVEY"

        fun getIntent(context: Context, category: Category, survey: Survey): Intent {
            val intent = Intent(context, SurveyActivity::class.java)
            intent.putExtra(EXTRA_CATEGORY, category)
            intent.putExtra(EXTRA_SURVEY, survey)
            return intent
        }
    }
}