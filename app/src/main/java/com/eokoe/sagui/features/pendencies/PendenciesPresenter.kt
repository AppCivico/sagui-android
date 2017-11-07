package com.eokoe.sagui.features.pendencies

import com.eokoe.sagui.data.entities.Pendency
import com.eokoe.sagui.data.model.SaguiModel
import com.eokoe.sagui.features.base.presenter.BasePresenterImpl
import com.eokoe.sagui.utils.LogUtil
import io.reactivex.observers.DisposableObserver

/**
 * @author Pedro Silva
 * @since 06/10/17
 */
class PendenciesPresenter constructor(private val saguiModel: SaguiModel)
    : BasePresenterImpl<PendenciesContract.View>(), PendenciesContract.Presenter {

    override fun list() =
            exec(saguiModel.listPendencies(), PendenciesObserver())

    inner class PendenciesObserver : DisposableObserver<List<Pendency>>() {
        override fun onNext(pendencies: List<Pendency>) {
            view?.loadPendencies(pendencies)
        }

        override fun onComplete() {

        }

        override fun onError(error: Throwable) {
            LogUtil.error(this, error)
        }
    }
}