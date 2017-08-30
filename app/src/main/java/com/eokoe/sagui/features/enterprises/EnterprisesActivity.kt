package com.eokoe.sagui.features.enterprises

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.data.model.impl.SurveyModelImpl
import com.eokoe.sagui.features.base.view.BaseActivityNavDrawer
import com.eokoe.sagui.features.base.view.ViewPresenter
import com.eokoe.sagui.features.dashboard.DashboardActivity
import kotlinx.android.synthetic.main.content_enterprises.*

class EnterprisesActivity : BaseActivityNavDrawer(), ViewPresenter<EnterprisesContract.Presenter>, EnterprisesContract.View {

    override lateinit var presenter: EnterprisesContract.Presenter
    private lateinit var enterprisesAdapter: EnterprisesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enterprises)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_filter -> {
                TODO("not implemented")
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        super.setUp(savedInstanceState)
        enterprisesAdapter = EnterprisesAdapter()
        presenter = EnterprisePresenter(SurveyModelImpl())
    }

    override fun init(savedInstanceState: Bundle?) {
        setupRecyclerView()
        presenter.list()
    }

    private fun setupRecyclerView() {
        rvEnterprises.adapter = enterprisesAdapter
        rvEnterprises.setHasFixedSize(true)
        enterprisesAdapter.onItemClickListener = object : EnterprisesAdapter.OnItemClickListener {
            override fun onClick(enterprise: Enterprise) {
                startActivity(DashboardActivity.getIntent(this@EnterprisesActivity, enterprise))
            }
        }
    }

    override fun load(enterprises: List<Enterprise>) {
        enterprisesAdapter.items = enterprises
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, EnterprisesActivity::class.java)
    }
}
