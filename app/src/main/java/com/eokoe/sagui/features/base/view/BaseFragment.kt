package com.eokoe.sagui.features.base.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.eokoe.sagui.features.base.presenter.BasePresenter

/**
 * @author Pedro Silva
 * @since 23/08/17
 */
abstract class BaseFragment: Fragment() {

    @Suppress("UNCHECKED_CAST")
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp(view, savedInstanceState)
        if (this is ViewPresenter<*>) {
            (this as ViewPresenter<BasePresenter<Any>>).presenter.attach(this)
        }
        init(view, savedInstanceState)
    }

    override fun onDestroy() {
        if (this is ViewPresenter<*>) {
            presenter.detach()
        }
        super.onDestroy()
    }

    open fun setUp(view: View?, savedInstanceState: Bundle?) {

    }

    abstract fun init(view: View?, savedInstanceState: Bundle?)
}