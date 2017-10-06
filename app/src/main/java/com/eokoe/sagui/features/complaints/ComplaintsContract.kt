package com.eokoe.sagui.features.complaints

import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Complaint
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.features.base.presenter.BasePresenter
import io.reactivex.Observable

/**
 * @author Pedro Silva
 * @since 27/09/17
 */
interface ComplaintsContract {
    interface View {
        fun loadComplaints(complaints: List<Complaint>)
        fun viewDetails()
    }

    interface Presenter : BasePresenter<View> {
        fun list(enterprise: Enterprise, category: Category?): Observable<List<Complaint>>
        fun allowNotification(allow: Boolean, complaintId: String)
    }
}