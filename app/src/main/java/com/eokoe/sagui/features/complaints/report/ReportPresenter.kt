package com.eokoe.sagui.features.complaints.report

import com.eokoe.sagui.data.entities.Complaint
import com.eokoe.sagui.data.model.SaguiModel
import com.eokoe.sagui.features.base.DefaultObserver
import com.eokoe.sagui.features.base.presenter.BasePresenterImpl
import io.reactivex.Observable

/**
 * @author Pedro Silva
 * @since 25/09/17
 */
class ReportPresenter constructor(private val saguiModel: SaguiModel)
    : ReportContract.Presenter, BasePresenterImpl<ReportContract.View>() {

    override fun saveComplaint(complaint: Complaint): Observable<Complaint> {
        return if (view?.isValidForm() == true) {
            exec(saguiModel.sendComplaint(complaint), ComplaintObservable())
        } else {
            Observable.empty()
        }
    }

    inner class ComplaintObservable : DefaultObserver<Complaint>(view) {
        override fun onSuccess(result: Complaint?) {
            if (result != null) {
                view?.onSaveSuccess(result)
            }
            view?.uploadAssets()
        }
    }
}