package com.eokoe.sagui.features.categories

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.data.model.impl.SurveyModelImpl
import com.eokoe.sagui.extensions.friendlyMessage
import com.eokoe.sagui.features.base.view.BaseActivityNavDrawer
import com.eokoe.sagui.features.base.view.RecyclerViewAdapter
import com.eokoe.sagui.features.base.view.ViewPresenter
import com.eokoe.sagui.features.surveys.list.CategoriesContract
import com.eokoe.sagui.features.surveys.list.CategoriesPresenter
import com.eokoe.sagui.features.surveys.list.SurveyListActivity
import kotlinx.android.synthetic.main.content_categories.*

/**
 * @author Pedro Silva
 * @since 16/08/17
 */
class CategoriesActivity : BaseActivityNavDrawer(),
        CategoriesContract.View, ViewPresenter<CategoriesContract.Presenter> {

    private lateinit var categoriesAdapter: CategoriesAdapter
    override lateinit var presenter: CategoriesContract.Presenter
    private var categories: ArrayList<Category>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        super.setUp(savedInstanceState)
        presenter = CategoriesPresenter(SurveyModelImpl())
        categoriesAdapter = CategoriesAdapter(categories == null)
        enterprise = intent.extras.getParcelable(EXTRA_ENTERPRISE)
    }

    override fun init(savedInstanceState: Bundle?) {
        title = enterprise!!.name

        if (categories == null) {
            presenter.list(enterprise!!)
        } else {
            categoriesAdapter.items = categories
        }
        setupRecyclerView()
    }

    override fun onResume() {
        navigationView.setCheckedItem(R.id.nav_home)
        super.onResume()
    }

    private fun setupRecyclerView() {
        val gridLayoutManager = GridLayoutManager(this, 3)
        gridLayoutManager.spanSizeLookup = categoriesAdapter.SpanSizeLookup()
        rvCategories.layoutManager = gridLayoutManager

        rvCategories.adapter = categoriesAdapter
        rvCategories.setHasFixedSize(true)
        categoriesAdapter.onItemClickListener = object : CategoriesAdapter.OnItemClickListener {
            override fun onClick(category: Category) {
                startActivity(SurveyListActivity.getIntent(this@CategoriesActivity, enterprise!!, category))
            }
        }
    }

    override fun load(categories: List<Category>) {
        this.categories = ArrayList(categories)
        categoriesAdapter.items = categories
    }

    override fun showLoading() {
        categoriesAdapter.isShowLoading = true
    }

    override fun hideLoading() {
        categoriesAdapter.isShowLoading = false
    }

    override fun showError(error: Throwable) {
        categoriesAdapter.showError(error.friendlyMessage, object : RecyclerViewAdapter.OnRetryClickListener {
            override fun retry() {
                categoriesAdapter.clearError()
                presenter.list(enterprise!!)
            }
        })
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            categories = savedInstanceState.getParcelableArrayList(STATE_CATEGORIES)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (categories != null) {
            outState.putParcelableArrayList(STATE_CATEGORIES, categories)
        }
    }

    companion object {
        private val EXTRA_ENTERPRISE = "EXTRA_ENTERPRISE"
        private val STATE_CATEGORIES = "STATE_CATEGORIES"

        @JvmStatic
        fun getIntent(context: Context, enterprise: Enterprise): Intent {
            val intent = Intent(context, CategoriesActivity::class.java)
            intent.putExtra(EXTRA_ENTERPRISE, enterprise)
            return intent
        }
    }
}