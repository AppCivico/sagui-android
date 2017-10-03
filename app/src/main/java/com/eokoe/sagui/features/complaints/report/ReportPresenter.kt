package com.eokoe.sagui.features.complaints.report

import com.eokoe.sagui.data.entities.Complaint
import com.eokoe.sagui.data.model.SaguiModel
import com.eokoe.sagui.features.base.presenter.BasePresenterImpl
import io.reactivex.Observable
import io.reactivex.observers.DisposableObserver

/**
 * @author Pedro Silva
 * @since 25/09/17
 */
class ReportPresenter constructor(private val saguiModel: SaguiModel)
    : ReportContract.Presenter, BasePresenterImpl<ReportContract.View>() {

    override fun saveComplaint(complaint: Complaint): Observable<Complaint> {
        view?.showLoading()
        return exec(saguiModel.sendComplaint(complaint), ComplaintObservable())
    }

    inner class ComplaintObservable : DisposableObserver<Complaint>() {
        override fun onNext(complaint: Complaint) {
            view?.onSaveSuccess(complaint)
        }

        override fun onComplete() {
            view?.uploadAssets()
            view?.hideLoading()
        }

        override fun onError(error: Throwable) {
            view?.showError(error)
        }

    }
}