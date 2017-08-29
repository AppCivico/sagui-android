package com.eokoe.sagui.features.base.view

import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.Toolbar

/**
 * @author Pedro Silva
 * @since 29/08/17
 */
interface NavigationDrawerView : NavigationView.OnNavigationItemSelectedListener {
    val toolbar: Toolbar
    val drawerLayout: DrawerLayout
    val navigationView: NavigationView
}