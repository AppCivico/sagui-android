package com.eokoe.sagui.data.net.services

import com.eokoe.sagui.data.entities.*
import io.reactivex.Observable
import okhttp3.MultipartBody
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

    @POST("confirmations")
    fun confirmComplaint(@Body confirmation: Confirmation): Observable<Confirmation>

    @Multipart
    @POST("complaints/{complaintId}/assets")
    fun sendComplaintAsset(@Path("complaintId") complaintId: String, @Part file: MultipartBody.Part): Observable<Asset>

    @Multipart
    @POST("confirmations/{confirmationId}/assets")
    fun sendConfirmationAsset(@Path("confirmationId") confirmationId: String, @Part file: MultipartBody.Part): Observable<Asset>
}