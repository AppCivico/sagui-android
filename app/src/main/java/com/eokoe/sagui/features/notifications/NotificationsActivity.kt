package com.eokoe.sagui.features.notifications

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.eokoe.sagui.R
import com.eokoe.sagui.features.base.view.BaseActivity
import kotlinx.android.synthetic.main.activity_notification.*

/**
 * @author Pedro Silva
 * @since 06/10/17
 */
class NotificationsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        super.setUp(savedInstanceState)
        showBackButton()
    }

    override fun init(savedInstanceState: Bundle?) {
        rvNotifications.adapter = NotificationsAdapter()
        rvNotifications.setHasFixedSize(true)
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, NotificationsActivity::class.java)
    }
}