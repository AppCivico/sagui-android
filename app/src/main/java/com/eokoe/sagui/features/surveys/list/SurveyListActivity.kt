package com.eokoe.sagui.features.surveys.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.data.entities.Survey
import com.eokoe.sagui.data.model.impl.SaguiModelImpl
import com.eokoe.sagui.extensions.friendlyMessage
import com.eokoe.sagui.features.base.view.BaseActivityNavDrawer
import com.eokoe.sagui.features.base.view.RecyclerViewAdapter
import com.eokoe.sagui.features.base.view.ViewPresenter
import com.eokoe.sagui.features.surveys.survey.SurveyActivity
import kotlinx.android.synthetic.main.activity_survey_list.*

/**
 * @author Pedro Silva
 * @since 16/08/17
 */
class SurveyListActivity : BaseActivityNavDrawer(),
        SurveyListContract.View, ViewPresenter<SurveyListContract.Presenter> {

    private lateinit var surveyListAdapter: SurveyListAdapter
    override lateinit var presenter: SurveyListContract.Presenter

    private var category: Category? = null
    private var surveys: ArrayList<Survey>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_list)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        super.setUp(savedInstanceState)
        showBackButton()
        enterprise = intent.extras.getParcelable(EXTRA_ENTERPRISE)
        category = intent.extras.getParcelable(EXTRA_CATEGORY)

        presenter = SurveyListPresenter(SaguiModelImpl())
        surveyListAdapter = SurveyListAdapter(surveys == null)
    }

    override fun init(savedInstanceState: Bundle?) {
        navigationView.setCheckedItem(R.id.nav_none)
        if (surveys == null) {
            presenter.list(category!!)
        } else {
            surveyListAdapter.items = surveys
        }
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        rvSurveys.adapter = surveyListAdapter
        rvSurveys.setHasFixedSize(true)
        surveyListAdapter.onItemClickListener = object : SurveyListAdapter.OnItemClickListener {
            override fun onClick(survey: Survey) {
                startActivity(SurveyActivity.getIntent(this@SurveyListActivity, category!!, survey))
            }
        }
    }

    override fun load(surveys: List<Survey>) {
        this.surveys = ArrayList(surveys)
        surveyListAdapter.items = surveys
    }

    override fun showLoading() {
        surveyListAdapter.isShowLoading = true
    }

    override fun hideLoading() {
        surveyListAdapter.isShowLoading = false
    }

    override fun showError(error: Throwable) {
        surveyListAdapter.showError(error.friendlyMessage, object : RecyclerViewAdapter.OnRetryClickListener {
            override fun retry() {
                surveyListAdapter.clearError()
                presenter.list(category!!)
            }
        })
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            surveys = savedInstanceState.getParcelableArrayList(STATE_SURVEYS)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (surveys != null) {
            outState.putParcelableArrayList(STATE_SURVEYS, surveys)
        }
    }

    companion object {
        private val EXTRA_ENTERPRISE = "EXTRA_ENTERPRISE"
        private val EXTRA_CATEGORY = "EXTRA_CATEGORY"
        private val STATE_SURVEYS = "STATE_SURVEYS"

        fun getIntent(context: Context, enterprise: Enterprise, category: Category): Intent {
            val intent = Intent(context, SurveyListActivity::class.java)
            intent.putExtra(EXTRA_ENTERPRISE, enterprise)
            intent.putExtra(EXTRA_CATEGORY, category)
            return intent
        }
    }
}