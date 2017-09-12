package com.eokoe.sagui.data.net.services

import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.data.entities.Survey
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * @author Pedro Silva
 */
interface SurveyService {
    @GET("enterprises")
    fun enterprises(): Observable<List<Enterprise>>

    @GET("enterprises/{enterprise}/axis")
    fun categories(@Path("enterprise") enterprise: String): Observable<List<Category>>

    @GET("surveys")
    fun surveys(@Query("axis_id") category: String): Observable<List<Survey>>
}