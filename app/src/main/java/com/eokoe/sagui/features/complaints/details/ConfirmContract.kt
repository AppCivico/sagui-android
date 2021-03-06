package com.eokoe.sagui.features.complaints.details

import com.eokoe.sagui.data.entities.Complaint
import com.eokoe.sagui.data.entities.Confirmation
import com.eokoe.sagui.data.entities.Notification
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
        fun onFilesSave(confirmation: Confirmation)
        fun onLoadComplaint(complaint: Complaint)
    }

    interface Presenter : BasePresenter<View> {
        fun getComplaint(id: String): Observable<Complaint>
        fun confirmComplaint(confirmation: Confirmation): Observable<Confirmation>
        fun updateConfirmation(confirmation: Confirmation): Observable<Confirmation>
        fun markAsRead(notificationId: String): Observable<Notification>
    }
}