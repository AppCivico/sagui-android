package com.eokoe.sagui.features.base.view

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.features.categories.CategoriesActivity
import com.eokoe.sagui.features.complaints.ComplaintsActivity
import com.eokoe.sagui.features.enterprises.EnterprisesActivity
import com.eokoe.sagui.features.help.HelpActivity
import com.eokoe.sagui.features.notifications.NotificationsActivity

/**
 * @author Pedro Silva
 * @since 29/08/17
 */
abstract class BaseActivityNavDrawer : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    protected var enterprise: Enterprise? = null
    protected var category: Category? = null
    protected var categories: ArrayList<Category>? = null

    protected val toolbar: Toolbar
        get() = findViewById(R.id.toolbar)
    protected val drawerLayout: DrawerLayout
        get() = findViewById(R.id.drawerLayout)
    protected val navigationView: NavigationView
        get() = findViewById(R.id.navigationView)

    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onPostCreate(savedInstanceState: Bundle?) {
        setSupportActionBar(toolbar)
        drawerToggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)

        super.onPostCreate(savedInstanceState)
    }

    override fun showBackButton() {
        drawerToggle.isDrawerIndicatorEnabled = false
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        drawerToggle.setToolbarNavigationClickListener {
            onBackPressed()
        }
    }

    fun showMenuButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        drawerToggle.isDrawerIndicatorEnabled = true
        drawerToggle.toolbarNavigationClickListener = null
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (!item.isChecked) {
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivityAndClearStack(CategoriesActivity.getIntent(this, enterprise!!))
                }
                R.id.nav_complaints -> {
                    val intent = if (category != null) {
                        ComplaintsActivity.getIntent(this, enterprise!!, category!!)
                    } else {
                        ComplaintsActivity.getIntent(this, enterprise!!, categories!!)
                    }
                    startActivity(intent)
                }
                R.id.nav_notifications -> {
                    startActivity(NotificationsActivity.getIntent(this))
                }
                R.id.nav_pending -> {
                }
                R.id.nav_change_development -> {
                    startActivityAndClearStack(EnterprisesActivity.getIntent(this))
                }
                R.id.nav_settings -> {
                }
                R.id.nav_help -> {
                    startActivity(HelpActivity.getIntent(this))
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            return true
        }
        return false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(STATE_ENTERPRISE, enterprise)
        outState.putParcelable(STATE_CATEGORY, category)
        outState.putParcelableArrayList(STATE_CATEGORIES, categories)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            enterprise = enterprise ?: savedInstanceState.getParcelable(STATE_ENTERPRISE)
            category = category ?: savedInstanceState.getParcelable(STATE_CATEGORY)
            categories = categories ?: savedInstanceState.getParcelableArrayList(STATE_CATEGORIES)
        }
    }

    companion object {
        private val STATE_ENTERPRISE = "STATE_ENTERPRISE"
        private val STATE_CATEGORY = "STATE_CATEGORY"
        private val STATE_CATEGORIES = "STATE_CATEGORIES"
    }
}