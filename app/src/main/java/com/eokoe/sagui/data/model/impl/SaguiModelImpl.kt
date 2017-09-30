package com.eokoe.sagui.data.model.impl

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.text.TextUtils
import com.eokoe.sagui.data.entities.*
import com.eokoe.sagui.data.exceptions.SaguiException
import com.eokoe.sagui.data.model.SaguiModel
import com.eokoe.sagui.data.net.ServiceGenerator
import com.eokoe.sagui.data.net.services.SaguiService
import io.reactivex.Observable
import io.realm.Realm
import java.io.IOException
import java.util.*

/**
 * @author Pedro Silva
 */
class SaguiModelImpl(val context: Context? = null) : SaguiModel {
    override fun selectEnterprise(enterprise: Enterprise): Observable<Enterprise> {
        return Observable.create { emitter ->
            Realm.getDefaultInstance().use { realm ->
                try {
                    enterprise.selected = true
                    realm.beginTransaction()
                    val enterprises = realm.where(Enterprise::class.java)
                            .equalTo("id", enterprise.id).or()
                            .equalTo("selected", true)
                            .findAll()
                    if (enterprises.isNotEmpty()) {
                        enterprises.map { it.selected = it.id == enterprise.id }
                    } else {
                        realm.insertOrUpdate(enterprise)
                    }
                    realm.commitTransaction()
                    emitter.onNext(enterprise)
                    emitter.onComplete()
                } catch (error: Exception) {
                    emitter.onError(error)
                }
            }
        }
    }

    override fun getSelectedEnterprise(): Observable<Enterprise> {
        return Observable.create { emitter ->
            Realm.getDefaultInstance().use { realm ->
                try {
                    val result = realm.where(Enterprise::class.java)
                            .equalTo("selected", true)
                            .findFirst()
                    if (result != null && result.isValid) {
                        emitter.onNext(realm.copyFromRealm(result))
                    }
                    emitter.onComplete()
                } catch (error: Exception) {
                    emitter.onError(error)
                }
            }
        }
    }

    override fun getEnterprises() =
            ServiceGenerator.getService(SaguiService::class.java).enterprises()

    override fun getCategories(enterprise: Enterprise) =
            ServiceGenerator.getService(SaguiService::class.java).categories(enterprise.id)

    override fun getSurveyList(category: Category): Observable<List<Survey>> =
            ServiceGenerator.getService(SaguiService::class.java)
                    .surveys(category.id)
                    .flatMapIterable {
                        return@flatMapIterable it
                    }
                    .filter {
                        return@filter it.questions != null && it.questions.isNotEmpty()
                    }
                    .map { survey ->
                        survey.questions?.forEach { question ->
                            question.answers?.forEach { answer ->
                                if (answer.image != null) {
                                    answer.image = ServiceGenerator.BASE_URL + ".." + answer.image
                                }
                            }
                        }
                        return@map survey
                    }
                    .toList()
                    .toObservable()

    override fun sendAnswers(submissions: Submissions): Observable<Submissions> {
        return ServiceGenerator.getService(SaguiService::class.java)
                .sendAnswers(submissions.surveyId!!, submissions)
                .map {
                    submissions.id = it.id
                    return@map submissions
                }
    }

    override fun saveComment(comment: Comment): Observable<Comment> {
        return ServiceGenerator.getService(SaguiService::class.java)
                .saveComment(comment.submissionsId!!, comment)
                .map {
                    comment.id = it.id
                    return@map comment
                }
    }

    override fun saveComplaint(complaint: Complaint): Observable<Complaint> {
        return ServiceGenerator.getService(SaguiService::class.java)
                .saveComplaint(complaint)
                .map {
                    complaint.id = it.id
                    return@map complaint
                }
    }

    override fun listComplaints(enterprise: Enterprise, category: Category?): Observable<List<Complaint>> {
        return ServiceGenerator.getService(SaguiService::class.java)
                .getComplaints(enterprise.id, category?.id)
    }

    override fun confirmComplaint(complaint: Complaint): Observable<Confirmation> {
        val confirmation = Confirmation(complaintId = complaint.id!!)
        return complaintConfirmed(complaint)
                .flatMap { confirmed ->
                    Observable.create<Complaint> { emitter ->
                        if (!confirmed) {
                            emitter.onNext(complaint)
                            emitter.onComplete()
                        } else {
                            emitter.onError(SaguiException("Você já enviou uma confirmação"))
                        }
                    }
                }
                .flatMap {
                    ServiceGenerator.getService(SaguiService::class.java)
                            .confirmComplaint(confirmation)
                }
                .map {
                    confirmation.id = it.id
                    return@map confirmation
                }
                .flatMap { confirm ->
                    Observable.create<Confirmation> { emitter ->
                        Realm.getDefaultInstance().use { realm ->
                            try {
                                realm.beginTransaction()
                                realm.insert(confirm)
                                realm.commitTransaction()
                                emitter.onNext(confirm)
                                emitter.onComplete()
                            } catch (error: Exception) {
                                emitter.onError(error)
                            }
                        }
                    }
                }
    }

    override fun complaintConfirmed(complaint: Complaint): Observable<Boolean> {
        return Observable.create { emitter ->
            Realm.getDefaultInstance().use { realm ->
                val result = realm.where(Confirmation::class.java)
                        .equalTo("complaintId", complaint.id!!)
                        .findFirst()
                emitter.onNext(result != null)
                emitter.onComplete()
            }
        }
    }

    override fun getAddressByLatLong(latLong: LatLong): Observable<String> {
        return Observable.create { emitter ->
            try {
                if (!Geocoder.isPresent()) {
                    throw Exception("no_geocoder_available")
                }
                val geocoder = Geocoder(context!!, Locale.getDefault())
                val addresses: List<Address>? = geocoder.getFromLocation(
                        latLong.latitude, latLong.longitude, 1)
                if (addresses == null || addresses.isEmpty())
                    throw Exception("no_address_found")

                val address = addresses[0]
                val addressFragments = ArrayList<String>()
                (0..address.maxAddressLineIndex)
                        .mapTo(addressFragments) {
                            address.getAddressLine(it)
                        }
                val addressText = TextUtils.join(System.getProperty("line.separator"), addressFragments)
                emitter.onNext(addressText)
                emitter.onComplete()
            } catch (error: IOException) {
                emitter.onError(Exception("service_not_available"))
            } catch (error: IllegalArgumentException) {
                emitter.onError(Exception("invalid_lat_long_used"))
            } catch (error: Exception) {
                emitter.onError(error)
            }
        }
    }
}