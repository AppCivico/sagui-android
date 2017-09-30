package com.eokoe.sagui.features.complaints.report.pin

import com.eokoe.sagui.data.entities.LatLong
import com.eokoe.sagui.features.base.presenter.BasePresenter
import com.eokoe.sagui.features.base.view.ViewError
import com.eokoe.sagui.features.base.view.ViewLoading
import io.reactivex.Observable

/**
 * @author Pedro Silva
 * @since 29/09/17
 */
interface PinContract {
    interface View : ViewLoading, ViewError {
        fun showAddress(address: String)
    }

    interface Presenter : BasePresenter<View> {
        fun findAddress(latLong: LatLong): Observable<String>
    }
}