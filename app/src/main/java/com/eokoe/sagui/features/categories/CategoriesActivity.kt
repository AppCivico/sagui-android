package com.eokoe.sagui.features.categories

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.extensions.friendlyMessage
import com.eokoe.sagui.features.base.view.BaseActivityNavDrawer
import com.eokoe.sagui.features.base.view.RecyclerViewAdapter
import com.eokoe.sagui.features.base.view.ViewPresenter
import com.eokoe.sagui.features.complaints.ComplaintsActivity
import com.eokoe.sagui.features.surveys.list.CategoriesContract
import com.eokoe.sagui.features.surveys.list.SurveyListActivity
import kotlinx.android.synthetic.main.activity_categories.*
import org.koin.android.ext.android.inject


/**
 * @author Pedro Silva
 * @since 16/08/17
 */
class CategoriesActivity : BaseActivityNavDrawer(), CategoryActionsDialog.OnActionClickListener,
        CategoriesContract.View, ViewPresenter<CategoriesContract.Presenter> {

    private val categoriesAdapter by inject<CategoriesAdapter>()
    override val presenter by inject<CategoriesContract.Presenter>()

    private var categoryActionsDialog: CategoryActionsDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        super.setUp(savedInstanceState)
        categoriesAdapter.isShowLoading = categories == null
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
        super.onResume()
        navigationView.setCheckedItem(R.id.nav_home)
    }

    override fun onBackPressed() {
        if (categoryActionsDialog?.isShow == true) {
            categoryActionsDialog?.dismiss()
        } else {
            super.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        val gridLayoutManager = GridLayoutManager(this, 3)
        gridLayoutManager.spanSizeLookup = categoriesAdapter.SpanSizeLookup()
        rvCategories.layoutManager = gridLayoutManager

        rvCategories.adapter = categoriesAdapter
        rvCategories.setHasFixedSize(true)
        categoriesAdapter.onItemClickListener = object : CategoriesAdapter.OnItemClickListener {
            override fun onClick(category: Category) {
                categoryActionsDialog = CategoryActionsDialog.newInstance(category)
                categoryActionsDialog?.show(supportFragmentManager)
            }
        }
    }

    override fun load(categories: List<Category>) {
        this.categories = ArrayList(categories)
        categoriesAdapter.items = categories
    }

    override fun onAnswerSurveyClick(category: Category) {
        startActivity(SurveyListActivity.getIntent(this@CategoriesActivity, enterprise!!, category))
    }

    override fun onSeeComplaintsClick(category: Category) {
        startActivity(ComplaintsActivity.getIntent(this@CategoriesActivity, enterprise!!, category))
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

    override fun saveInstanceState(outState: Bundle) {
        if (categories != null) {
            outState.putParcelableArrayList(STATE_CATEGORIES, categories)
        }
    }

    override fun restoreInstanceState(savedInstanceState: Bundle) {
        categories = savedInstanceState.getParcelableArrayList(STATE_CATEGORIES)
    }

    companion object {
        val TAG = CategoriesActivity::class.simpleName!!
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