package com.eokoe.sagui.features.surveys.categories.survey_list

import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Survey
import com.eokoe.sagui.features.base.presenter.BasePresenter
import com.eokoe.sagui.features.base.view.ViewError
import com.eokoe.sagui.features.base.view.ViewLoading
import io.reactivex.Observable

/**
 * @author Pedro Silva
 * @since 23/08/17
 */
interface SurveyListContract {
    interface View : ViewLoading, ViewError {
        fun load(surveys: List<Survey>)
    }

    interface Presenter : BasePresenter<View> {
        fun list(category: Category): Observable<List<Survey>>
    }
}