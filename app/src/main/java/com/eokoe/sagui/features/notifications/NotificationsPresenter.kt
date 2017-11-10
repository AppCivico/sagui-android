package com.eokoe.sagui.features.notifications

import com.eokoe.sagui.data.entities.Notification
import com.eokoe.sagui.data.model.SaguiModel
import com.eokoe.sagui.features.base.DefaultObserver
import com.eokoe.sagui.features.base.presenter.BasePresenterImpl

/**
 * @author Pedro Silva
 * @since 06/10/17
 */
class NotificationsPresenter constructor(private val saguiModel: SaguiModel)
    : BasePresenterImpl<NotificationContract.View>(), NotificationContract.Presenter {

    override fun list() = exec(saguiModel.listUnreadNotifications(), NotificationsObserver())

    inner class NotificationsObserver : DefaultObserver<List<Notification>>(view) {
        override fun onSuccess(result: List<Notification>?) {
            if (result != null) {
                view?.loadNotifications(result)
            }
        }
    }
}