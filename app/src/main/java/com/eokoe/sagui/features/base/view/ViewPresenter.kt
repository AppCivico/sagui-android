package com.eokoe.sagui.features.base.view

import com.eokoe.sagui.features.base.presenter.BasePresenter

/**
 * @author Pedro Silva
 */
interface ViewPresenter<out Presenter: BasePresenter<*>> {
    val presenter: Presenter
}