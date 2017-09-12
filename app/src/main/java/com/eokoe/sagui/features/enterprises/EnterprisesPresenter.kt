package com.eokoe.sagui.features.enterprises

import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.data.model.SurveyModel
import com.eokoe.sagui.features.base.presenter.BasePresenterImpl
import io.reactivex.Observable
import io.reactivex.observers.DisposableObserver

/**
 * @author Pedro Silva
 * @since 29/08/17
 */
class EnterprisesPresenter constructor(private val surveyModel: SurveyModel)
    : BasePresenterImpl<EnterprisesContract.View>(), EnterprisesContract.Presenter {

    override fun setEnterprise(enterprise: Enterprise) =
            exec(surveyModel.selectEnterprise(enterprise), SaveEnterpriseObserver())

    override fun list(): Observable<List<Enterprise>> {
        view?.showLoading()
        return exec(surveyModel.getEnterprises(), EnterprisesObserver())
    }

    inner class EnterprisesObserver : DisposableObserver<List<Enterprise>>() {
        override fun onNext(enterprises: List<Enterprise>) {
            view?.load(enterprises)
        }

        override fun onComplete() {
            view?.hideLoading()
        }

        override fun onError(e: Throwable) {
            view?.showError(e)
        }
    }

    inner class SaveEnterpriseObserver : DisposableObserver<Enterprise>() {
        override fun onNext(enterprise: Enterprise) {
            view?.navigateToDashboard(enterprise)
        }

        override fun onComplete() {
        }

        override fun onError(e: Throwable) {
        }
    }
}