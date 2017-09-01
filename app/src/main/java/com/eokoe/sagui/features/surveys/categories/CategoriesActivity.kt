package com.eokoe.sagui.features.surveys.categories

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.features.base.view.BaseActivityNavDrawer
import com.eokoe.sagui.features.surveys.categories.survey_list.SurveyListFragment

/**
 * @author Pedro Silva
 * @since 16/08/17
 */
class CategoriesActivity : BaseActivityNavDrawer(),
        CategoriesFragment.OnCategoryClickListener, FragmentManager.OnBackStackChangedListener {

    private var contentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)
        supportFragmentManager.addOnBackStackChangedListener(this)
    }

    override fun onDestroy() {
        supportFragmentManager.removeOnBackStackChangedListener(this)
        super.onDestroy()
    }

    override fun setUp(savedInstanceState: Bundle?) {
        super.setUp(savedInstanceState)
        showBackButton()
        enterprise = intent.extras.getParcelable(EXTRA_ENTERPRISE)
    }

    override fun init(savedInstanceState: Bundle?) {
        navigationView.setCheckedItem(R.id.nav_survey)
        if (contentFragment == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.content, CategoriesFragment.newInstance(enterprise!!))
                    .commit()
        }
    }

    override fun onCategoryClick(category: Category) {
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                        R.anim.enter_from_right, R.anim.exit_to_left,
                        R.anim.enter_from_left, R.anim.exit_to_right
                )
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

    companion object {
        private val EXTRA_ENTERPRISE = "EXTRA_ENTERPRISE"
        private val STATE_FRAGMENT = "STATE_FRAGMENT"

        fun getIntent(context: Context, enterprise: Enterprise): Intent {
            val intent = Intent(context, CategoriesActivity::class.java)
            intent.putExtra(EXTRA_ENTERPRISE, enterprise)
            return intent
        }
    }
}