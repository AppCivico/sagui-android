package com.eokoe.sagui.data.net.services

import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.data.entities.Survey
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author Pedro Silva
 */
interface SurveyService {
    @GET("enterprises")
    fun enterprises(): Observable<List<Enterprise>>

    @GET("categories")
    fun categories(@Query("enterprise") enterprise: String): Observable<List<Category>>

    @GET("surveys")
    fun surveys(@Query("category") category: Int): Observable<List<Survey>>
}