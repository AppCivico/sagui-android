package com.eokoe.sagui.features.survey.note

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.eokoe.sagui.R
import com.eokoe.sagui.features.base.view.BaseActivity
import kotlinx.android.synthetic.main.activity_note.*

/**
 * @author Pedro Silva
 * @since 30/08/17
 */
class NoteActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        super.setUp(savedInstanceState)
        showBackButton()
    }

    override fun init(savedInstanceState: Bundle?) {
        btnSend.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, NoteActivity::class.java)
    }
}