package com.eokoe.sagui.features.help

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.eokoe.sagui.R
import com.eokoe.sagui.features.base.view.BaseActivity
import kotlinx.android.synthetic.main.activity_help.*

/**
 * @author Pedro Silva
 * @since 12/10/17
 */
class HelpActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        showBackButton()
    }

    override fun init(savedInstanceState: Bundle?) {
        rvHelp.setHasFixedSize(true)
        rvHelp.adapter = HelpAdapter()
    }

    override fun saveInstanceState(outState: Bundle) {}

    override fun restoreInstanceState(savedInstanceState: Bundle) {}

    companion object {
        fun getIntent(context: Context) = Intent(context, HelpActivity::class.java)
    }
}