package com.eokoe.sagui.features.base.presenter

/**
 * @author Pedro Silva
 * @since 16/08/17
 */
interface BasePresenter<in View> {
    fun attach(view: View)
    fun detach()
}