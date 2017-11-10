package com.eokoe.sagui.features.pendencies

import com.eokoe.sagui.data.entities.Pendency
import com.eokoe.sagui.data.model.SaguiModel
import com.eokoe.sagui.features.base.DefaultObserver
import com.eokoe.sagui.features.base.presenter.BasePresenterImpl

/**
 * @author Pedro Silva
 * @since 06/10/17
 */
class PendenciesPresenter constructor(private val saguiModel: SaguiModel)
    : BasePresenterImpl<PendenciesContract.View>(), PendenciesContract.Presenter {

    override fun list() = exec(saguiModel.listPendencies(), PendenciesObserver())

    inner class PendenciesObserver : DefaultObserver<List<Pendency>>(view) {
        override fun onSuccess(result: List<Pendency>?) {
            view?.loadPendencies(result!!)
        }
    }
}