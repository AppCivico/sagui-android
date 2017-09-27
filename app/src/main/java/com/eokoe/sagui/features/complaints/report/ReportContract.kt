package com.eokoe.sagui.features.complaints.report

import com.eokoe.sagui.data.entities.Complaint
import com.eokoe.sagui.features.base.presenter.BasePresenter
import com.eokoe.sagui.features.base.view.ViewError
import com.eokoe.sagui.features.base.view.ViewLoading
import io.reactivex.Observable

/**
 * @author Pedro Silva
 * @since 25/09/17
 */
interface ReportContract {
    interface View : ViewLoading, ViewError {
        fun onSaveSuccess(complaint: Complaint)
    }

    interface Presenter : BasePresenter<View> {
        fun saveComplaint(complaint: Complaint): Observable<Complaint>
    }
}