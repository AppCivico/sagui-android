package com.eokoe.sagui.features.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.eokoe.sagui.R
import com.eokoe.sagui.features.base.view.BaseActivityNavDrawer
import com.eokoe.sagui.features.categories.CategoriesActivity
import kotlinx.android.synthetic.main.content_dashboard.*

/**
 * @author Pedro Silva
 * @since 25/08/17
 */
class DashboardActivity : BaseActivityNavDrawer() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
    }

    override fun init(savedInstanceState: Bundle?) {
        civAnswerSurvey.setOnClickListener { navigateToSurveys() }
        btnAnswerSurvey.setOnClickListener { navigateToSurveys() }
    }

    private fun navigateToSurveys() {
        startActivity(CategoriesActivity.getIntent(this))
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, DashboardActivity::class.java)
    }
}