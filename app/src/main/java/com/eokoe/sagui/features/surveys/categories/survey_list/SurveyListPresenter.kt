package com.eokoe.sagui.features.surveys.categories.survey_list

import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Survey
import com.eokoe.sagui.data.model.SurveyModel
import com.eokoe.sagui.features.base.presenter.BasePresenterImpl
import io.reactivex.observers.DisposableObserver

/**
 * @author Pedro Silva
 * @since 23/08/17
 */
class SurveyListPresenter constructor(private val surveyModel: SurveyModel)
    : SurveyListContract.Presenter, BasePresenterImpl<SurveyListContract.View>() {

    override fun list(category: Category) =
            exec(surveyModel.getSurveyList(category), SurveyListObserver())

    inner class SurveyListObserver : DisposableObserver<List<Survey>>() {
        override fun onNext(surveyList: List<Survey>) {
            view?.load(surveyList)
        }

        override fun onComplete() {
        }

        override fun onError(e: Throwable) {
            TODO("not implemented")
        }
    }
}