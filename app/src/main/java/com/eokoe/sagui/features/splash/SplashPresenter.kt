package com.eokoe.sagui.features.splash

import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.data.model.SaguiModel
import com.eokoe.sagui.features.base.presenter.BasePresenterImpl
import io.reactivex.observers.DisposableObserver

/**
 * @author Pedro Silva
 * @since 30/08/17
 */
class SplashPresenter(private val saguiModel: SaguiModel)
    : BasePresenterImpl<SplashContract.View>(), SplashContract.Presenter {

    override fun getEnterprise() =
            exec(saguiModel.getSelectedEnterprise(), EnterpriseObserver())

    inner class EnterpriseObserver : DisposableObserver<Enterprise>() {
        var enterprise: Enterprise? = null
        override fun onNext(enterprise: Enterprise) {
            this.enterprise = enterprise
        }

        override fun onComplete() {
            if (enterprise != null) {
                view?.setEnterprise(enterprise!!)
            } else {
                view?.onEmptyEnterprise()
            }
        }

        override fun onError(e: Throwable) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}