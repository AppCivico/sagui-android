package com.eokoe.sagui.features.base

import com.eokoe.sagui.features.base.view.ViewError
import com.eokoe.sagui.features.base.view.ViewLoading
import com.eokoe.sagui.utils.LogUtil
import io.reactivex.observers.DisposableObserver

/**
 * @author Pedro Silva
 * @since 09/11/17
 */
abstract class DefaultObserver<T>(
        private val view: Any?,
        private val omitLoading: Boolean = false,
        private val omitError: Boolean = false
) : DisposableObserver<T>() {

    protected var result: T? = null
        private set

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
        LogUtil.error(this, error)
        if (!omitError) {
            (view as? ViewError)?.showError(error)
        }
        onHideLoading()
    }

    open fun onShowLoading() {
        if (!omitLoading) {
            (view as? ViewLoading)?.showLoading()
        }
    }

    open fun onHideLoading() {
        if (!omitLoading) {
            (view as? ViewLoading)?.hideLoading()
        }
    }

    abstract fun onSuccess(result: T?)
}