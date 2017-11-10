package com.eokoe.sagui.features.surveys.list

import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Survey
import com.eokoe.sagui.data.model.SaguiModel
import com.eokoe.sagui.features.base.DefaultObserver
import com.eokoe.sagui.features.base.presenter.BasePresenterImpl

/**
 * @author Pedro Silva
 * @since 23/08/17
 */
class SurveyListPresenter constructor(private val saguiModel: SaguiModel)
    : SurveyListContract.Presenter, BasePresenterImpl<SurveyListContract.View>() {

    override fun list(category: Category) =
            exec(saguiModel.getSurveyList(category), SurveyListObserver())

    inner class SurveyListObserver : DefaultObserver<List<Survey>>(view) {
        override fun onSuccess(result: List<Survey>?) {
            if (result != null) {
                view?.load(result)
            }
        }
    }
}