package com.eokoe.sagui.features.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.eokoe.sagui.R
import com.eokoe.sagui.features.base.view.BaseActivity
import com.eokoe.sagui.features.categories.CategoriesActivity
import kotlinx.android.synthetic.main.activity_dashboard.*

/**
 * @author Pedro Silva
 * @since 25/08/17
 */
class DashboardActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
    }

    override fun init(savedInstanceState: Bundle?) {
        civAnswerSurvey.setImageResource(R.drawable.ic_answer_survey)
        civAnswerSurvey.setOnClickListener { navigateToSurveys() }
        btnAnswerSurvey.setOnClickListener { navigateToSurveys() }

        civSeeNotes.setImageResource(R.drawable.ic_see_notes)
    }

    private fun navigateToSurveys() {
        startActivity(CategoriesActivity.getIntent(this))
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, DashboardActivity::class.java)
    }
}