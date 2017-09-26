package com.eokoe.sagui.features.complaints.report

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Complaint
import com.eokoe.sagui.data.model.impl.SaguiModelImpl
import com.eokoe.sagui.features.base.view.BaseActivity
import com.eokoe.sagui.features.base.view.ViewPresenter
import com.eokoe.sagui.features.complaints.report.ReportAdapter.ItemType
import com.eokoe.sagui.features.complaints.report.pin.PinActivity
import kotlinx.android.synthetic.main.activity_report.*

/**
 * @author Pedro Silva
 * @since 25/09/17
 */
class ReportActivity : BaseActivity(),
        ReportContract.View, ViewPresenter<ReportContract.Presenter> {

    override lateinit var presenter: ReportContract.Presenter
    lateinit var reportAdapter: ReportAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        showBackButton()
        presenter = ReportPresenter(SaguiModelImpl())
    }

    override fun init(savedInstanceState: Bundle?) {
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        rvReport.setHasFixedSize(false)
        reportAdapter = ReportAdapter()
        rvReport.adapter = reportAdapter
        reportAdapter.onItemClickListener = object : ReportAdapter.OnItemClickListener {
            override fun onItemClick(itemType: ItemType) {
                when(itemType) {
                    ItemType.LOCATION ->
                        startActivityForResult(PinActivity.getIntent(this@ReportActivity), REQUEST_CODE_LOCATION)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.save_check, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_ok) {
            val complaint = Complaint(description = reportAdapter.description)
            presenter.saveComplaint(complaint)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showError(error: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSaveSuccess(complaint: Complaint) {
        hideKeyboard()
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun showLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        private val REQUEST_CODE_LOCATION = 1

        fun getIntent(context: Context) = Intent(context, ReportActivity::class.java)
    }
}