package com.eokoe.sagui.features.pendencies

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.eokoe.sagui.R
import com.eokoe.sagui.features.base.view.BaseActivity

/**
 * @author Pedro Silva
 * @since 06/11/17
 */
class PendenciesActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pendencies)
    }

    override fun init(savedInstanceState: Bundle?) {
        // TODO
    }

    override fun saveInstanceState(outState: Bundle) {
        // TODO
    }

    override fun restoreInstanceState(savedInstanceState: Bundle) {
        // TODO
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, PendenciesActivity::class.java)
    }
}