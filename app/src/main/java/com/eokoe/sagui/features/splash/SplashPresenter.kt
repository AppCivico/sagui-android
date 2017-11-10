package com.eokoe.sagui.features.splash

import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.data.model.SaguiModel
import com.eokoe.sagui.features.base.DefaultObserver
import com.eokoe.sagui.features.base.presenter.BasePresenterImpl

/**
 * @author Pedro Silva
 * @since 30/08/17
 */
class SplashPresenter(private val saguiModel: SaguiModel)
    : BasePresenterImpl<SplashContract.View>(), SplashContract.Presenter {

    override fun getEnterprise() = exec(saguiModel.getSelectedEnterprise(), EnterpriseObserver())

    inner class EnterpriseObserver : DefaultObserver<Enterprise>(view) {
        override fun onSuccess(result: Enterprise?) {
            if (result != null) {
                view?.setEnterprise(result)
            } else {
                view?.onEmptyEnterprise()
            }
        }
    }
}