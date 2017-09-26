package com.eokoe.sagui.features.complaints.report

import com.eokoe.sagui.data.entities.Complaint
import com.eokoe.sagui.data.model.SaguiModel
import com.eokoe.sagui.features.base.presenter.BasePresenterImpl

/**
 * @author Pedro Silva
 * @since 25/09/17
 */
class ReportPresenter constructor(private val saguiModel: SaguiModel)
    : ReportContract.Presenter, BasePresenterImpl<ReportContract.View>() {

    override fun saveComplaint(complaint: Complaint) {
        // TODO
//        view?.onSaveSuccess(complaint)
    }
}