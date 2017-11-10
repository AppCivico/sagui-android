package com.eokoe.sagui.features.surveys.list

import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.data.model.SaguiModel
import com.eokoe.sagui.features.base.DefaultObserver
import com.eokoe.sagui.features.base.presenter.BasePresenterImpl

/**
 * @author Pedro Silva
 * @since 16/08/17
 */
class CategoriesPresenter constructor(private val saguiModel: SaguiModel)
    : CategoriesContract.Presenter, BasePresenterImpl<CategoriesContract.View>() {

    override fun list(enterprise: Enterprise) =
            exec(saguiModel.getCategories(enterprise), CategoriesObserver())

    inner class CategoriesObserver : DefaultObserver<List<Category>>(view) {
        override fun onSuccess(result: List<Category>?) {
            if (result != null) {
                view?.load(result)
            }
        }
    }
}