package com.eokoe.sagui.features.complaints

import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Complaint
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.data.model.SaguiModel
import com.eokoe.sagui.features.base.presenter.BasePresenterImpl
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.observers.DisposableObserver

/**
 * @author Pedro Silva
 * @since 27/09/17
 */
class ComplaintsPresenter constructor(private val saguiModel: SaguiModel)
    : ComplaintsContract.Presenter, BasePresenterImpl<ComplaintsContract.View>() {

    override fun list(enterprise: Enterprise, category: Category?) =
            exec(saguiModel.listComplaints(enterprise, category), ComplaintsObserver())

    override fun allowNotification(allow: Boolean, complaintId: String) {
        if (allow) {
            FirebaseMessaging.getInstance().subscribeToTopic("complaint-$complaintId")
        }
    }

    inner class ComplaintsObserver : DisposableObserver<List<Complaint>>() {
        override fun onNext(complaints: List<Complaint>) {
            view?.loadComplaints(complaints)
        }

        override fun onComplete() {
        }

        override fun onError(e: Throwable) {
        }

    }
}