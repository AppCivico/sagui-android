package com.eokoe.sagui.features.splash

import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.features.base.presenter.BasePresenter
import io.reactivex.Observable

/**
 * @author Pedro Silva
 * @since 30/08/17
 */
interface SplashContract {
    interface View {
        fun setEnterprise(enterprise: Enterprise)
        fun onEmptyEnterprise()
    }

    interface Presenter : BasePresenter<View> {
        fun getEnterprise(): Observable<Enterprise>
    }
}