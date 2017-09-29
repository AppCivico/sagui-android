package com.eokoe.sagui.features.complaints.details

import com.eokoe.sagui.data.entities.Complaint
import com.eokoe.sagui.data.entities.Confirmation
import com.eokoe.sagui.features.base.presenter.BasePresenter
import com.eokoe.sagui.features.base.view.ViewError
import com.eokoe.sagui.features.base.view.ViewLoading
import io.reactivex.Observable

/**
 * @author Pedro Silva
 */
interface ConfirmContract {
    interface View : ViewLoading, ViewError {
        fun onComplaintConfirmed(confirmation: Confirmation)
    }

    interface Presenter : BasePresenter<View> {
        fun confirmComplaint(complaint: Complaint): Observable<Confirmation>
    }
}