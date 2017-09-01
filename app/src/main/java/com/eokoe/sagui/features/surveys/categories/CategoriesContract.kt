package com.eokoe.sagui.features.surveys.categories

import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.features.base.presenter.BasePresenter
import io.reactivex.Observable

/**
 * @author Pedro Silva
 * @since 16/08/17
 */
interface CategoriesContract {
    interface View {
        fun load(categories: List<Category>)
    }

    interface Presenter : BasePresenter<View> {
        fun list(enterprise: Enterprise): Observable<List<Category>>
    }
}