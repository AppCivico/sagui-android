package com.eokoe.sagui.features.categories.survey_list

import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Survey
import com.eokoe.sagui.features.base.presenter.BasePresenter
import io.reactivex.Observable

/**
 * @author Pedro Silva
 * @since 23/08/17
 */
interface SurveyListContract {
    interface View {
        fun load(surveys: List<Survey>)
    }

    interface Presenter : BasePresenter<View> {
        fun list(category: Category): Observable<List<Survey>>
    }
}