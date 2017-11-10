package com.eokoe.sagui.features.pendencies

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Pendency
import com.eokoe.sagui.features.base.view.BaseActivity
import com.eokoe.sagui.features.base.view.ViewPresenter
import com.eokoe.sagui.features.complaints.report.ReportActivity
import kotlinx.android.synthetic.main.activity_pendencies.*
import org.koin.android.ext.android.inject

/**
 * @author Pedro Silva
 * @since 06/11/17
 */
class PendenciesActivity : BaseActivity(),
        ViewPresenter<PendenciesContract.Presenter>, PendenciesContract.View {

    override val presenter by inject<PendenciesContract.Presenter>()
    private val pendenciesAdapter by inject<PendenciesAdapter>()

    private var pendecies: List<Pendency>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pendencies)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        super.setUp(savedInstanceState)
        showBackButton()
    }

    override fun init(savedInstanceState: Bundle?) {
        rvPendencies.adapter = pendenciesAdapter
        rvPendencies.setHasFixedSize(true)
        pendenciesAdapter.onItemClickListener = object : PendenciesAdapter.OnItemClickListener {
            override fun onItemClick(pendency: Pendency) {
                val intent = ReportActivity.getIntent(this@PendenciesActivity, pendency.complaint)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.list()
    }

    override fun loadPendencies(pendencies: List<Pendency>) {
        this.pendecies = pendencies
        pendenciesAdapter.items = pendencies
    }

    override fun saveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList(STATE_PENDENCIES, ArrayList<Pendency>(pendecies))
    }

    override fun restoreInstanceState(savedInstanceState: Bundle) {
        pendecies = savedInstanceState.getParcelableArrayList(STATE_PENDENCIES)
    }

    companion object {
        val TAG = PendenciesActivity::class.simpleName!!
        private val STATE_PENDENCIES = "STATE_PENDENCIES"

        fun getIntent(context: Context) = Intent(context, PendenciesActivity::class.java)
    }
}