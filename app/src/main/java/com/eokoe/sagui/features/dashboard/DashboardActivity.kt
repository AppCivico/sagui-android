package com.eokoe.sagui.features.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.features.base.view.BaseActivityNavDrawer
import com.eokoe.sagui.features.surveys.categories.CategoriesActivity
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
        enterprise = intent.extras.getParcelable(EXTRA_ENTERPRISE)
        title = enterprise!!.name

        civAnswerSurvey.setOnClickListener { navigateToSurveys() }
        btnAnswerSurvey.setOnClickListener { navigateToSurveys() }
    }

    override fun onResume() {
        navigationView.setCheckedItem(R.id.nav_none)
        super.onResume()
    }

    private fun navigateToSurveys() {
        startActivity(CategoriesActivity.getIntent(this, enterprise!!))
    }

    companion object {
        val EXTRA_ENTERPRISE = "EXTRA_ENTERPRISE"

        @JvmStatic
        fun getIntent(context: Context, enterprise: Enterprise): Intent {
            val intent = Intent(context, DashboardActivity::class.java)
            intent.putExtra(EXTRA_ENTERPRISE, enterprise)
            return intent
        }
    }
}