package com.eokoe.sagui.features.base

import com.eokoe.sagui.features.base.view.ViewError
import com.eokoe.sagui.features.base.view.ViewLoading
import io.reactivex.observers.DisposableObserver

/**
 * @author Pedro Silva
 * @since 09/11/17
 */
abstract class DefaultObserver<T>(private val view: Any?) : DisposableObserver<T>() {
    var result: T? = null

    init {
        onShowLoading()
    }

    override fun onNext(result: T) {
        this.result = result
    }

    override fun onComplete() {
        onSuccess(result)
        onHideLoading()
    }

    override fun onError(error: Throwable) {
        (view as? ViewError)?.showError(error)
        onHideLoading()
    }

    open fun onShowLoading() {
        (view as? ViewLoading)?.showLoading()
    }

    open fun onHideLoading() {
        (view as? ViewLoading)?.hideLoading()
    }

    abstract fun onSuccess(result: T?)
}