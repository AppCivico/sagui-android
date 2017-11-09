package com.eokoe.sagui.features.pendencies

import com.eokoe.sagui.data.entities.Notification
import com.eokoe.sagui.data.entities.Pendency
import com.eokoe.sagui.features.base.presenter.BasePresenter
import io.reactivex.Observable

/**
 * @author Pedro Silva
 * @since 06/10/17
 */
interface PendenciesContract {
    interface View {
        fun loadPendencies(pendencies: List<Pendency>)
    }
    interface Presenter : BasePresenter<View> {
        fun list(): Observable<List<Pendency>>
    }
}