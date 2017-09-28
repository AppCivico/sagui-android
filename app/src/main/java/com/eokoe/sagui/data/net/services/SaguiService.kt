package com.eokoe.sagui.data.net.services

import com.eokoe.sagui.data.entities.*
import io.reactivex.Observable
import retrofit2.http.*

/**
 * @author Pedro Silva
 */
interface SaguiService {
    @GET("enterprises")
    fun enterprises(): Observable<List<Enterprise>>

    @GET("enterprises/{enterprise}/axis")
    fun categories(@Path("enterprise") enterprise: String): Observable<List<Category>>

    @GET("surveys")
    fun surveys(@Query("axis_id") category: String): Observable<List<Survey>>

    @POST("surveys/{survey_id}/submissions")
    fun sendAnswers(@Path("survey_id") surveyId: String, @Body submissions: Submissions): Observable<Submissions>

    @POST("submissions/{submissions_id}/comments")
    fun saveComment(@Path("submissions_id") submissionsId: String, @Body comment: Comment): Observable<Comment>

    @POST("complaints")
    fun saveComplaint(@Body complaint: Complaint): Observable<Complaint>

    @GET("enterprises/{enterprise}/complaints")
    fun getComplaints(@Path("enterprise") enterpriseId: String, @Query("axis_id") category: String?): Observable<List<Complaint>>
}