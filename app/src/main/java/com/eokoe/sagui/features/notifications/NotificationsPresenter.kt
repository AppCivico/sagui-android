package com.eokoe.sagui.features.notifications

import com.eokoe.sagui.data.entities.Notification
import com.eokoe.sagui.data.model.SaguiModel
import com.eokoe.sagui.features.base.presenter.BasePresenterImpl
import com.eokoe.sagui.utils.LogUtil
import io.reactivex.observers.DisposableObserver

/**
 * @author Pedro Silva
 * @since 06/10/17
 */
class NotificationsPresenter constructor(private val saguiModel: SaguiModel)
    : BasePresenterImpl<NotificationContract.View>(), NotificationContract.Presenter {

    override fun list() =
            exec(saguiModel.listUnreadNotifications(), NotificationsObserver())

    inner class NotificationsObserver: DisposableObserver<List<Notification>>() {
        override fun onNext(notifications: List<Notification>) {
            view?.loadNotifications(notifications)
        }

        override fun onComplete() {

        }

        override fun onError(error: Throwable) {
            LogUtil.error(this, error)
        }
    }
}