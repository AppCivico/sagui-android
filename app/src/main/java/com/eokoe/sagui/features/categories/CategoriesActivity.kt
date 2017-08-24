package com.eokoe.sagui.features.categories

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.features.base.view.BaseActivity
import com.eokoe.sagui.features.categories.survey_list.SurveyListFragment

/**
 * @author Pedro Silva
 * @since 16/08/17
 */
class CategoriesActivity : BaseActivity(),
        CategoriesFragment.OnCategoryClickListener, FragmentManager.OnBackStackChangedListener {

    private var contentFragment: Fragment? = null

    companion object {
        private val STATE_FRAGMENT = "STATE_FRAGMENT"

        fun getIntent(context: Context): Intent = Intent(context, CategoriesActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)
        supportFragmentManager.addOnBackStackChangedListener(this)
    }

    override fun onDestroy() {
        supportFragmentManager.removeOnBackStackChangedListener(this)
        super.onDestroy()
    }

    override fun init(savedInstanceState: Bundle?) {
        if (contentFragment == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.content, CategoriesFragment.newInstance())
                    .commit()
        }
    }

    override fun onCategoryClick(category: Category) {
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                        R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.content, SurveyListFragment.newInstance(category))
                .addToBackStack(SurveyListFragment.TAG)
                .commit()
    }

    override fun onBackStackChanged() {
        contentFragment = supportFragmentManager.findFragmentById(R.id.content)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            contentFragment = supportFragmentManager.getFragment(savedInstanceState, STATE_FRAGMENT)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (contentFragment != null) {
            supportFragmentManager.putFragment(outState, STATE_FRAGMENT, contentFragment)
        }
    }
}