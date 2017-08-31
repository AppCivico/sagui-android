package com.eokoe.sagui.features.enterprises.filter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.eokoe.sagui.R
import com.eokoe.sagui.features.base.view.BaseActivity
import kotlinx.android.synthetic.main.activity_enterprises_filter.*

/**
 * @author Pedro Silva
 * @since 31/08/17
 */
class EnterprisesFilterActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enterprises_filter)
    }

    override fun init(savedInstanceState: Bundle?) {
        showBackButton()
        btnApply.setOnClickListener {
            finish()
        }
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, EnterprisesFilterActivity::class.java)
    }
}