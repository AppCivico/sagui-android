package com.eokoe.sagui.features

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.eokoe.sagui.R
import com.eokoe.sagui.features.base.view.BaseActivityNavDrawer

class MainActivity : BaseActivityNavDrawer() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_filter -> {
                // TODO filter
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun init(savedInstanceState: Bundle?) {

    }
}
