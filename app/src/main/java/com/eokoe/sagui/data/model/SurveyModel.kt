package com.eokoe.sagui.data.model

import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Survey
import io.reactivex.Observable

/**
 * @author Pedro Silva
 */
interface SurveyModel {
    fun getCategories(): Observable<List<Category>>

    fun getSurveyList(category: Category): Observable<List<Survey>>
}