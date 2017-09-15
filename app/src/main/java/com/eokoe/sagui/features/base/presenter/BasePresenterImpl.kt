package com.eokoe.sagui.features.base.presenter

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.lang.ref.WeakReference

/**
 * @author Pedro Silva
 * @since 16/08/17
 */
open class BasePresenterImpl<View>: BasePresenter<View> {
    private var viewReference: WeakReference<View>? = null
    private val disposables = CompositeDisposable()

    protected var view: View? = null
        get() = viewReference?.get()
        private set

    override fun attach(view: View) {
        viewReference = WeakReference(view)
    }

    override fun detach() {
        viewReference?.clear()
        if (!disposables.isDisposed) {
            disposables.dispose()
        }
    }

    fun <T> exec(observable: Observable<T>, disposable: DisposableObserver<T>): Observable<T> {
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(disposable)
        addDisposable(disposable)
        return observable
    }

    private fun addDisposable(disposable: Disposable) {
        disposables.add(disposable)
    }
}