package com.eokoe.sagui.features.notifications

import com.eokoe.sagui.data.entities.Notification
import com.eokoe.sagui.features.base.presenter.BasePresenter
import io.reactivex.Observable

/**
 * @author Pedro Silva
 * @since 06/10/17
 */
interface NotificationContract {
    interface View {
        fun loadNotifications(notifications: List<Notification>)
    }
    interface Presenter : BasePresenter<View> {
        fun list(): Observable<List<Notification>>
    }
}