package com.eokoe.sagui.features.enterprises

import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.features.base.presenter.BasePresenter
import io.reactivex.Observable

/**
 * @author Pedro Silva
 * @since 29/08/17
 */
interface EnterprisesContract {
    interface View {
        fun load(enterprises: List<Enterprise>)
    }

    interface Presenter : BasePresenter<View> {
        fun list(): Observable<List<Enterprise>>
    }
}