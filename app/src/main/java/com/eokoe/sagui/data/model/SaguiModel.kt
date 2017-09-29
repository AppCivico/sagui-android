package com.eokoe.sagui.data.model

import com.eokoe.sagui.data.entities.*
import io.reactivex.Observable

/**
 * @author Pedro Silva
 */
interface SaguiModel {
    fun selectEnterprise(enterprise: Enterprise): Observable<Enterprise>

    fun getSelectedEnterprise(): Observable<Enterprise>

    fun getEnterprises(): Observable<List<Enterprise>>

    fun getCategories(enterprise: Enterprise): Observable<List<Category>>

    fun getSurveyList(category: Category): Observable<List<Survey>>

    fun sendAnswers(submissions: Submissions): Observable<Submissions>

    fun saveComment(comment: Comment): Observable<Comment>

    fun saveComplaint(complaint: Complaint): Observable<Complaint>

    fun listComplaints(enterprise: Enterprise, category: Category?): Observable<List<Complaint>>

    fun confirmComplaint(complaint: Complaint): Observable<Confirmation>
}