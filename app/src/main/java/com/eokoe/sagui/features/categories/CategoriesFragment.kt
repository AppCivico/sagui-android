package com.eokoe.sagui.features.categories

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.model.impl.SurveyModelImpl
import com.eokoe.sagui.features.base.view.BaseFragment
import com.eokoe.sagui.features.base.view.ViewPresenter
import kotlinx.android.synthetic.main.fragment_categories.*

/**
 * @author Pedro Silva
 * @since 23/08/17
 */
class CategoriesFragment: BaseFragment(),
        CategoriesContract.View, ViewPresenter<CategoriesContract.Presenter> {

    private lateinit var categoriesAdapter: CategoriesAdapter
    override lateinit var presenter: CategoriesContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_categories, container, false)
    }

    override fun setUp(view: View?, savedInstanceState: Bundle?) {
        super.setUp(view, savedInstanceState)
        presenter = CategoriesPresenter(SurveyModelImpl())
        categoriesAdapter = CategoriesAdapter()
    }

    override fun init(view: View?, savedInstanceState: Bundle?) {
        presenter.list()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        rvCategories.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvCategories.adapter = categoriesAdapter
        rvCategories.setHasFixedSize(true)
        categoriesAdapter.onItemClickListener = object : CategoriesAdapter.OnItemClickListener {
            override fun onClick(category: Category) {
                if (activity is OnCategoryClickListener) {
                    (activity as OnCategoryClickListener).onCategoryClick(category)
                }
//                startActivity(SurveyActivity.getIntent(activity, category))
            }
        }
    }

    override fun load(items: List<Category>) {
        categoriesAdapter.items = items
    }

    companion object {
        fun newInstance() = CategoriesFragment()
    }

    interface OnCategoryClickListener {
        fun onCategoryClick(category: Category)
    }
}