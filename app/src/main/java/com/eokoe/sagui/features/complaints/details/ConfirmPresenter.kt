package com.eokoe.sagui.features.complaints.details

import com.eokoe.sagui.data.entities.Complaint
import com.eokoe.sagui.data.entities.Confirmation
import com.eokoe.sagui.data.entities.Notification
import com.eokoe.sagui.data.model.SaguiModel
import com.eokoe.sagui.features.base.DefaultObserver
import com.eokoe.sagui.features.base.EmptyObserver
import com.eokoe.sagui.features.base.presenter.BasePresenterImpl

/**
 * @author Pedro Silva
 */
class ConfirmPresenter constructor(private val saguiModel: SaguiModel)
    : ConfirmContract.Presenter, BasePresenterImpl<ConfirmContract.View>() {
    override fun updateConfirmation(confirmation: Confirmation) =
            exec(saguiModel.confirmationFiles(confirmation), ConfirmationFilesObserver())

    override fun confirmComplaint(confirmation: Confirmation) =
            exec(saguiModel.confirmComplaint(confirmation), ConfirmationObserver())

    override fun getComplaint(id: String) = exec(saguiModel.getComplaint(id), ComplaintObserver())

    override fun markAsRead(notificationId: String) =
            exec(saguiModel.markAsRead(Notification(id = notificationId)), EmptyObserver())

    inner class ComplaintObserver : DefaultObserver<Complaint>(view, omitLoading = true) {
        override fun onSuccess(result: Complaint?) {
            view?.onLoadComplaint(result!!)
        }
    }

    inner class ConfirmationObserver : DefaultObserver<Confirmation>(view) {
        override fun onSuccess(result: Confirmation?) {
            view?.onComplaintConfirmed(result!!)
        }
    }

    inner class ConfirmationFilesObserver : DefaultObserver<Confirmation>(view) {
        override fun onSuccess(result: Confirmation?) {
            if (result != null) view?.onFilesSave(result)
        }
    }
}