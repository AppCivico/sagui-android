package com.eokoe.sagui.features.complaints.report.pin

import com.eokoe.sagui.data.entities.LatLong
import com.eokoe.sagui.data.model.SaguiModel
import com.eokoe.sagui.features.base.DefaultObserver
import com.eokoe.sagui.features.base.presenter.BasePresenterImpl
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * @author Pedro Silva
 * @since 29/09/17
 */
class PinPresenter constructor(private val saguiModel: SaguiModel)
    : PinContract.Presenter, BasePresenterImpl<PinContract.View>() {

    private val disposables = CompositeDisposable()

    override fun findAddress(latLong: LatLong): Observable<String> {
        val observer = AddressObserver()
        val observable = Observable.just(latLong)
                .debounce(300, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    saguiModel.getAddressByLatLong(latLong)
                }
        observable.subscribeWith(observer)
        disposables.add(observer)
        return observable
    }

    override fun detach() {
        super.detach()
        disposables.dispose()
    }

    inner class AddressObserver : DefaultObserver<String>(view) {
        override fun onSuccess(result: String?) {
            if (result != null) {
                view?.showAddress(result)
            }
        }
    }
}