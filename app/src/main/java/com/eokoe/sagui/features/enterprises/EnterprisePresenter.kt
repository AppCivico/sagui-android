package com.eokoe.sagui.features.enterprises

import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.data.model.SurveyModel
import com.eokoe.sagui.features.base.presenter.BasePresenterImpl
import io.reactivex.observers.DisposableObserver

/**
 * @author Pedro Silva
 * @since 29/08/17
 */
class EnterprisePresenter constructor(private val surveyModel: SurveyModel)
    : BasePresenterImpl<EnterprisesContract.View>(), EnterprisesContract.Presenter {

    override fun list() = exec(surveyModel.getEnterprises(), EnterprisesObserver())

    inner class EnterprisesObserver : DisposableObserver<List<Enterprise>>() {
        override fun onNext(enterprises: List<Enterprise>) {
            view?.load(enterprises)
        }

        override fun onComplete() {

        }

        override fun onError(e: Throwable) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}