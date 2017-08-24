package com.eokoe.sagui.features.categories

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.features.base.view.BaseActivity
import com.eokoe.sagui.features.categories.survey_list.SurveyListFragment

/**
 * @author Pedro Silva
 * @since 16/08/17
 */
class CategoriesActivity : BaseActivity(), CategoriesFragment.OnCategoryClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)
    }

    override fun init(savedInstanceState: Bundle?) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.content, CategoriesFragment.newInstance())
                .commit()
    }

    override fun onCategoryClick(category: Category) {
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                        R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.content, SurveyListFragment.newInstance(category))
                .addToBackStack(SurveyListFragment.TAG)
                .commit()
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, CategoriesActivity::class.java)
    }
}