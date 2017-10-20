package com.eokoe.sagui.features.help.faq

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.eokoe.sagui.R
import com.eokoe.sagui.features.base.view.BaseActivity
import kotlinx.android.synthetic.main.activity_faq.*

/**
 * @author Pedro Silva
 * @since 20/10/17
 */
class FaqActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        showBackButton()
    }

    override fun init(savedInstanceState: Bundle?) {
        rvFaq.setHasFixedSize(true)
        rvFaq.adapter = FaqAdapter()
    }

    override fun saveInstanceState(outState: Bundle) {
    }

    override fun restoreInstanceState(savedInstanceState: Bundle) {
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, FaqActivity::class.java)
    }
}