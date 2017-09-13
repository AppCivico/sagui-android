package com.eokoe.sagui.features.surveys.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.data.model.impl.SurveyModelImpl
import com.eokoe.sagui.features.base.view.BaseFragment
import com.eokoe.sagui.features.base.view.ViewPresenter
import kotlinx.android.synthetic.main.fragment_categories.*

/**
 * @author Pedro Silva
 * @since 23/08/17
 */
class CategoriesFragment : BaseFragment(),
        CategoriesContract.View, ViewPresenter<CategoriesContract.Presenter> {

    private lateinit var categoriesAdapter: CategoriesAdapter
    override lateinit var presenter: CategoriesContract.Presenter

    private lateinit var enterprise: Enterprise
    private var categories: ArrayList<Category>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_categories, container, false)
    }

    override fun setUp(view: View?, savedInstanceState: Bundle?) {
        super.setUp(view, savedInstanceState)
        presenter = CategoriesPresenter(SurveyModelImpl())
        categoriesAdapter = CategoriesAdapter(categories == null)
        enterprise = arguments.getParcelable(EXTRA_ENTERPRISE)
    }

    override fun init(view: View?, savedInstanceState: Bundle?) {
        if (categories == null) {
            presenter.list(enterprise)
        } else {
            categoriesAdapter.items = categories
        }
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        rvCategories.adapter = categoriesAdapter
        rvCategories.setHasFixedSize(true)
        categoriesAdapter.onItemClickListener = object : CategoriesAdapter.OnItemClickListener {
            override fun onClick(category: Category) {
                if (activity is OnCategoryClickListener) {
                    (activity as OnCategoryClickListener).onCategoryClick(category)
                }
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

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
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

        fun newInstance(enterprise: Enterprise): CategoriesFragment {
            val fragment = CategoriesFragment()
            fragment.arguments = Bundle()
            fragment.arguments.putParcelable("EXTRA_ENTERPRISE", enterprise)
            return fragment
        }
    }

    interface OnCategoryClickListener {
        fun onCategoryClick(category: Category)
    }
}