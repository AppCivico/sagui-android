package com.eokoe.sagui.features.enterprises

import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.data.model.SaguiModel
import com.eokoe.sagui.features.base.DefaultObserver
import com.eokoe.sagui.features.base.presenter.BasePresenterImpl

/**
 * @author Pedro Silva
 * @since 29/08/17
 */
class EnterprisesPresenter constructor(private val saguiModel: SaguiModel)
    : BasePresenterImpl<EnterprisesContract.View>(), EnterprisesContract.Presenter {

    override fun setEnterprise(enterprise: Enterprise) =
            exec(saguiModel.selectEnterprise(enterprise), SaveEnterpriseObserver())

    override fun list() = exec(saguiModel.getEnterprises(), EnterprisesObserver())

    inner class EnterprisesObserver : DefaultObserver<List<Enterprise>>(view) {
        override fun onSuccess(result: List<Enterprise>?) {
            if (result != null) {
                view?.load(result)
            }
        }
    }

    inner class SaveEnterpriseObserver
        : DefaultObserver<Enterprise>(view, omitLoading = true, omitError = true) {
        override fun onSuccess(result: Enterprise?) {
            view?.navigateToDashboard(result!!)
        }
    }
}