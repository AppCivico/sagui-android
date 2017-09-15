package com.eokoe.sagui.features.base.view

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.features.enterprises.EnterprisesActivity
import com.eokoe.sagui.features.surveys.categories.CategoriesActivity

/**
 * @author Pedro Silva
 * @since 29/08/17
 */
abstract class BaseActivityNavDrawer : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    protected var enterprise: Enterprise? = null
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
        when (item.itemId) {
            R.id.nav_survey -> {
                startActivity(CategoriesActivity.getIntent(this, enterprise!!))
            }
            R.id.nav_complaints -> {
            }
            R.id.nav_notifications -> {
            }
            R.id.nav_pending -> {
            }
            R.id.nav_change_development -> {
                startActivityAndClearStack(EnterprisesActivity.getIntent(this))
            }
            R.id.nav_settings -> {
            }
            R.id.nav_help -> {
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}