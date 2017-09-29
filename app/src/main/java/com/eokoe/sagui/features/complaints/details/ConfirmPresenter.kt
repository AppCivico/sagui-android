package com.eokoe.sagui.features.complaints.details

import com.eokoe.sagui.data.entities.Complaint
import com.eokoe.sagui.data.entities.Confirmation
import com.eokoe.sagui.data.model.SaguiModel
import com.eokoe.sagui.features.base.presenter.BasePresenterImpl
import io.reactivex.Observable
import io.reactivex.observers.DisposableObserver

/**
 * @author Pedro Silva
 */
class ConfirmPresenter constructor(private val saguiModel: SaguiModel)
    : ConfirmContract.Presenter, BasePresenterImpl<ConfirmContract.View>() {

    override fun confirmComplaint(complaint: Complaint): Observable<Confirmation> {
        view?.showLoading()
        return exec(saguiModel.confirmComplaint(complaint), ConfirmationObserver())
    }

    inner class ConfirmationObserver : DisposableObserver<Confirmation>() {
        override fun onNext(confirmation: Confirmation) {
            view?.onComplaintConfirmed(confirmation)
        }

        override fun onComplete() {
            view?.hideLoading()
        }

        override fun onError(error: Throwable) {
            view?.showError(error)
        }

    }
}