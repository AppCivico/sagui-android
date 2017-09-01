package com.eokoe.sagui.features.surveys.categories

import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.data.model.SurveyModel
import com.eokoe.sagui.features.base.presenter.BasePresenterImpl
import io.reactivex.observers.DisposableObserver

/**
 * @author Pedro Silva
 * @since 16/08/17
 */
class CategoriesPresenter constructor(private val surveyModel: SurveyModel)
    : CategoriesContract.Presenter, BasePresenterImpl<CategoriesContract.View>() {

    override fun list(enterprise: Enterprise) =
            exec(surveyModel.getCategories(enterprise), CategoriesObserver())

    inner class CategoriesObserver : DisposableObserver<List<Category>>() {
        override fun onNext(categories: List<Category>) {
            view?.load(categories)
        }

        override fun onComplete() {
        }

        override fun onError(e: Throwable) {
            TODO("not implemented")
        }
    }
}