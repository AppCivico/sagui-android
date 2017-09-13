package com.eokoe.sagui.features.surveys.categories.survey_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Survey
import com.eokoe.sagui.data.model.impl.SurveyModelImpl
import com.eokoe.sagui.features.base.view.BaseFragment
import com.eokoe.sagui.features.base.view.ViewPresenter
import com.eokoe.sagui.features.surveys.survey.SurveyActivity
import kotlinx.android.synthetic.main.fragment_survey_list.*

/**
 * @author Pedro Silva
 * @since 23/08/17
 */
class SurveyListFragment: BaseFragment(),
        SurveyListContract.View, ViewPresenter<SurveyListContract.Presenter> {

    private lateinit var surveyListAdapter: SurveyListAdapter
    override lateinit var presenter: SurveyListContract.Presenter

    private var category: Category? = null
    private var surveys: ArrayList<Survey>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_survey_list, container, false)
    }

    override fun setUp(view: View?, savedInstanceState: Bundle?) {
        super.setUp(view, savedInstanceState)
        presenter = SurveyListPresenter(SurveyModelImpl())
        surveyListAdapter = SurveyListAdapter(surveys == null)
    }

    override fun init(view: View?, savedInstanceState: Bundle?) {
        category = arguments.getParcelable(EXTRA_CATEGORY)
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
                startActivity(SurveyActivity.getIntent(activity, category!!, survey))
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

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
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
        val TAG = "SurveyListFragment"

        private val EXTRA_CATEGORY = "EXTRA_CATEGORY"
        private val STATE_SURVEYS = "STATE_SURVEYS"

        fun newInstance(category: Category): SurveyListFragment {
            val fragment = SurveyListFragment()
            fragment.arguments = Bundle()
            fragment.arguments.putParcelable(EXTRA_CATEGORY, category)
            return fragment
        }

    }
}