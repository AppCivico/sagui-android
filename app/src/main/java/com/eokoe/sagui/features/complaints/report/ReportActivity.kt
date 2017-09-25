package com.eokoe.sagui.features.complaints.report

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import com.eokoe.sagui.R
import com.eokoe.sagui.features.base.view.BaseActivity
import kotlinx.android.synthetic.main.activity_report.*

/**
 * @author Pedro Silva
 * @since 25/09/17
 */
class ReportActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        showBackButton()
    }

    override fun init(savedInstanceState: Bundle?) {
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        rvReport.setHasFixedSize(true)
        rvReport.adapter = ReportAdapter()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.save_check, menu)
        return true
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, ReportActivity::class.java)
    }
}