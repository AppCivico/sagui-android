package com.eokoe.sagui.features.base

import io.reactivex.observers.DisposableObserver

/**
 * @author Pedro Silva
 */
open class EmptyObserver<T>: DisposableObserver<T>() {
    override fun onNext(t: T) {}

    override fun onComplete() {}

    override fun onError(e: Throwable) {}
}