package com.eokoe.sagui.data.model.impl

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.text.TextUtils
import com.eokoe.sagui.data.entities.*
import com.eokoe.sagui.data.exceptions.SaguiException
import com.eokoe.sagui.data.model.SaguiModel
import com.eokoe.sagui.data.net.ServiceGenerator
import com.eokoe.sagui.data.net.services.SaguiService
import com.eokoe.sagui.extensions.getMimeType
import com.eokoe.sagui.extensions.toFile
import io.reactivex.Observable
import io.realm.Realm
import io.realm.RealmList
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    override fun sendComplaint(complaint: Complaint): Observable<Complaint> {
        return ServiceGenerator.getService(SaguiService::class.java)
                .saveComplaint(complaint)
                .map {
                    complaint.id = it.id
                    complaint.files.map {
                        it.complaintId = complaint.id!!
                        it
                    }
                    complaint
                }
                .flatMap {
                    Observable.create<Complaint> { emitter ->
                        Realm.getDefaultInstance().use { realm ->
                            try {
                                realm.beginTransaction()
                                realm.insertOrUpdate(it)
                                realm.commitTransaction()
                                emitter.onNext(complaint)
                                emitter.onComplete()
                            } catch (error: Exception) {
                                emitter.onError(error)
                            }
                        }
                    }
                }
    }

    override fun listComplaints(enterprise: Enterprise, category: Category?): Observable<List<Complaint>> {
        val complaintsApi = ServiceGenerator.getService(SaguiService::class.java)
                .getComplaints(enterprise.id, category?.id)

        val complaintsDB = Observable.create<List<Complaint>> { emitter ->
            Realm.getDefaultInstance().use { realm ->
                val query = realm.where(Complaint::class.java)
                        .equalTo("enterpriseId", enterprise.id)
                if (category != null) {
                    query.equalTo("categoryId", category.id)
                }
                val results = query.findAll()
                if (results != null) {
                    emitter.onNext(realm.copyFromRealm(results))
                }
                emitter.onComplete()
            }
        }
        return Observable.concat(complaintsDB, complaintsApi)
    }

    override fun confirmComplaint(complaint: Complaint): Observable<Confirmation> {
        val confirmation = Confirmation(complaintId = complaint.id!!)
        return isComplaintConfirmed(complaint)
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
                    confirmation
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

    override fun isComplaintConfirmed(complaint: Complaint): Observable<Boolean> {
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

    override fun sendComplaintAsset(asset: Asset): Observable<Asset> {
        return Observable
                .create<Uri> { emitter ->
                    // TODO
                    /*val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    val bitmap = BitmapFactory.decodeFile(asset.uri.encodedPath, options)
                    if (options.outWidth != -1 && options.outHeight != -1) {
                        val bos = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, bos)
                        val input = ByteArrayInputStream(bos.toByteArray())
                    } else {
                    }*/
                    emitter.onNext(asset.uri)
                    emitter.onComplete()
                }
                .map { uri ->
                    val file = uri.toFile(context!!)
                    if (file != null && file.exists()) {
                        val mimeType = uri.getMimeType(context)
                        val requestFile = RequestBody.create(MediaType.parse(mimeType), file)
                        MultipartBody.Part.createFormData("file", file.name, requestFile)
                    } else null
                }
                .flatMap {
                    ServiceGenerator.getService(SaguiService::class.java)
                            .sendAsset(asset.complaintId, it)
                }
                .map {
                    asset.id = it.id
                    asset.sent = true
                    asset.remotePath = it.remotePath
                    asset.type = it.type
                    asset
                }
                .flatMap {
                    Observable.create<Asset> { emitter ->
                        Realm.getDefaultInstance().use { realm ->
                            try {
                                realm.beginTransaction()
                                val result = realm.where(Complaint::class.java)
                                        .equalTo("id", it.complaintId)
                                        .findFirst()

                                val complaint = realm.copyFromRealm(result)
                                val files = complaint.files.map {
                                    if (it.localPath == asset.localPath) asset
                                    else it
                                }
                                complaint.files = RealmList(*files.toTypedArray())
                                realm.copyToRealmOrUpdate(complaint)
                                realm.commitTransaction()
                                emitter.onNext(it)
                                emitter.onComplete()
                            } catch (error: Exception) {
                                emitter.onError(error)
                            }
                        }
                    }
                }
    }

    override fun getComplaint(complaintId: String): Observable<Complaint> {
        TODO("not implemented")
    }

    override fun getAssetsPendingUpload(): Observable<List<Asset>> {
        return Observable
                .create<List<Complaint>> { emitter ->
                    Realm.getDefaultInstance().use { realm ->
                        val result = realm.where(Complaint::class.java)
                                .equalTo("files.sent", false)
                                .findAll()
                        if (result != null && result.isNotEmpty()) {
                            emitter.onNext(realm.copyFromRealm(result))
                        }
                        emitter.onComplete()
                    }
                }
                .flatMapIterable { it }
                .flatMapIterable { it.files }
                .filter { !it.sent }
                .toList()
                .toObservable()
    }
}