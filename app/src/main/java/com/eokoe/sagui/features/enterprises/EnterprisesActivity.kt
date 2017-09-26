package com.eokoe.sagui.features.enterprises

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.data.model.impl.SaguiModelImpl
import com.eokoe.sagui.extensions.friendlyMessage
import com.eokoe.sagui.features.base.view.BaseActivityNavDrawer
import com.eokoe.sagui.features.base.view.RecyclerViewAdapter
import com.eokoe.sagui.features.base.view.ViewPresenter
import com.eokoe.sagui.features.categories.CategoriesActivity
import com.eokoe.sagui.features.enterprises.filter.EnterprisesFilterActivity
import kotlinx.android.synthetic.main.content_enterprises.*

class EnterprisesActivity : BaseActivityNavDrawer(), ViewPresenter<EnterprisesContract.Presenter>, EnterprisesContract.View {

    override lateinit var presenter: EnterprisesContract.Presenter
    private lateinit var enterprisesAdapter: EnterprisesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enterprises)
    }

    // TODO create filter layout
    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_filter -> {
                openFilter()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openFilter() {
        startActivityForResult(EnterprisesFilterActivity.getIntent(this), REQUEST_FILTER)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        super.setUp(savedInstanceState)
        enterprisesAdapter = EnterprisesAdapter(true)
        presenter = EnterprisesPresenter(SaguiModelImpl())
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
                presenter.setEnterprise(enterprise)
            }
        }
    }

    override fun load(enterprises: List<Enterprise>) {
        enterprisesAdapter.items = enterprises
    }

    override fun showLoading() {
        enterprisesAdapter.isShowLoading = true
    }

    override fun hideLoading() {
        enterprisesAdapter.isShowLoading = false
    }

    override fun showError(error: Throwable) {
        enterprisesAdapter.showError(error.friendlyMessage, object : RecyclerViewAdapter.OnRetryClickListener {
            override fun retry() {
                enterprisesAdapter.clearError()
                presenter.list()
            }
        })
    }

    override fun navigateToDashboard(enterprise: Enterprise) {
        startActivity(CategoriesActivity.getIntent(this, enterprise))
    }

    companion object {
        val REQUEST_FILTER = 1
        fun getIntent(context: Context) = Intent(context, EnterprisesActivity::class.java)
    }
}
