package com.eokoe.sagui.features.notifications

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Notification
import com.eokoe.sagui.data.model.impl.SaguiModelImpl
import com.eokoe.sagui.features.base.view.BaseActivity
import com.eokoe.sagui.features.base.view.ViewPresenter
import kotlinx.android.synthetic.main.activity_notification.*

/**
 * @author Pedro Silva
 * @since 06/10/17
 */
class NotificationsActivity : BaseActivity(),
        ViewPresenter<NotificationContract.Presenter>, NotificationContract.View{

    override lateinit var presenter: NotificationContract.Presenter
    private lateinit var notificationsAdapter: NotificationsAdapter

    private var notifications: List<Notification>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
    }

    override fun setUp(savedInstanceState: Bundle?) {
        super.setUp(savedInstanceState)
        showBackButton()
        presenter = NotificationsPresenter(SaguiModelImpl())
    }

    override fun init(savedInstanceState: Bundle?) {
        notificationsAdapter = NotificationsAdapter()
        rvNotifications.adapter = notificationsAdapter
        rvNotifications.setHasFixedSize(true)
        notificationsAdapter.onItemClickListener = object : NotificationsAdapter.OnItemClickListener {
            override fun onItemClick(notification: Notification) {
                // TODO
            }
        }
        presenter.list()
    }

    override fun loadNotifications(notifications: List<Notification>) {
        this.notifications = notifications
        notificationsAdapter.items = notifications
    }

    override fun saveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList(STATE_NOTIFICATIONS, ArrayList<Notification>(notifications))
    }

    override fun restoreInstanceState(savedInstanceState: Bundle) {
        notifications = savedInstanceState.getParcelableArrayList(STATE_NOTIFICATIONS)
    }

    companion object {
        private val STATE_NOTIFICATIONS = "STATE_NOTIFICATIONS"

        fun getIntent(context: Context) = Intent(context, NotificationsActivity::class.java)
    }
}