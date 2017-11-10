package com.eokoe.sagui.data.model.impl

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.text.TextUtils
import com.eokoe.sagui.data.entities.*
import com.eokoe.sagui.data.exceptions.SaguiException
import com.eokoe.sagui.data.model.SaguiModel
import com.eokoe.sagui.data.net.services.SaguiService
import com.eokoe.sagui.extensions.getMimeType
import com.eokoe.sagui.extensions.toFile
import io.reactivex.Observable
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmModel
import io.realm.RealmQuery
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author Pedro Silva
 */
class SaguiModelImpl(
        private val context: Context,
        private val saguiService: SaguiService
) : SaguiModel {

    private fun <T : RealmModel> save(t: T, hasPk: Boolean = false): Observable<T> {
        return Observable.create { emitter ->
            Realm.getDefaultInstance().use { realm ->
                try {
                    realm.beginTransaction()
                    if (hasPk) realm.insertOrUpdate(t)
                    else realm.insert(t)
                    realm.commitTransaction()
                    emitter.onNext(t)
                    emitter.onComplete()
                } catch (error: Exception) {
                    emitter.onError(error)
                }
            }
        }
    }

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
                        realm.copyToRealmOrUpdate(enterprise)
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

    override fun getEnterprises() = saguiService.enterprises()

    override fun getCategories(enterprise: Enterprise) = saguiService.categories(enterprise.id)

    override fun getSurveyList(category: Category): Observable<List<Survey>> =
            saguiService.surveys(category.id)
                    .flatMapIterable { it }
                    .filter { it.questions != null && it.questions.isNotEmpty() }
                    .flatMap { setHasAnswer(it) }
                    .toList()
                    .toObservable()

    private fun setHasAnswer(survey: Survey): Observable<Survey> {
        return Observable.create<Survey> { emitter ->
            Realm.getDefaultInstance().use { realm ->
                try {
                    val result = realm.where(Submissions::class.java)
                            .equalTo("surveyId", survey.id)
                            .findFirst()
                    survey.hasAnswer = result != null
                    emitter.onNext(survey)
                    emitter.onComplete()
                } catch (error: Exception) {
                    emitter.onError(error)
                }
            }
        }
    }

    override fun hasAnswer(survey: Survey): Observable<Boolean> =
            setHasAnswer(survey).map { survey.hasAnswer }

    override fun sendAnswers(submissions: Submissions): Observable<Submissions> {
        return save(submissions)
                .flatMap { saguiService.sendAnswers(submissions.surveyId!!, submissions) }
                .map {
                    submissions.id = it.id
                    submissions
                }
    }

    override fun saveComment(comment: Comment): Observable<Comment> {
        return saguiService.saveComment(comment.submissionsId!!, comment)
                .map {
                    comment.id = it.id
                    comment
                }
    }

    override fun sendComplaint(complaint: Complaint): Observable<Complaint> {
        complaint.categoryId = complaint.category?.id
        if (complaint.pk.isEmpty()) {
            complaint.pk = UUID.randomUUID().toString()
        }
        return if (complaint.location == null)
            save(complaint, true)
        else saguiService.saveComplaint(complaint)
                .map {
                    complaint.id = it.id
                    complaint.files.map {
                        it.parentId = complaint.id!!
                        it.parentType = Asset.ParentType.COMPLAINT
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
        val complaintsApi = saguiService.getComplaints(enterprise.id, category?.id)

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

    override fun confirmComplaint(confirmation: Confirmation): Observable<Confirmation> {
        return isComplaintConfirmed(confirmation.complaintId)
                .flatMap { confirmed ->
                    Observable.create<Confirmation> { emitter ->
                        if (!confirmed) {
                            emitter.onNext(confirmation)
                            emitter.onComplete()
                        } else {
                            emitter.onError(SaguiException("Você já enviou uma confirmação"))
                        }
                    }
                }
                .flatMap {
                    saguiService.confirmComplaint(it)
                }
                .map {
                    confirmation.id = it.id
                    confirmation.files.map {
                        it.parentId = confirmation.id!!
                        it.parentType = Asset.ParentType.CONFIRMATION
                        it
                    }
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

    override fun isComplaintConfirmed(complaintId: String): Observable<Boolean> {
        return Observable.create { emitter ->
            Realm.getDefaultInstance().use { realm ->
                val result = realm.where(Confirmation::class.java)
                        .equalTo("complaintId", complaintId)
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
                val geocoder = Geocoder(context, Locale.getDefault())
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

    override fun sendAsset(asset: Asset): Observable<Asset> {
        return Observable.just(asset.uri)
                .map { uri ->
                    val file = uri.toFile(context)
                    if (file != null && file.exists()) {
                        val mimeType = uri.getMimeType(context)
                        val requestFile = RequestBody.create(MediaType.parse(mimeType), file)
                        MultipartBody.Part.createFormData("file", file.name, requestFile)
                    } else null
                }
                .flatMap {
                    if (asset.parentType == Asset.ParentType.COMPLAINT) {
                        saguiService.sendComplaintAsset(asset.parentId, it)
                    } else {
                        saguiService.sendConfirmationAsset(asset.parentId, it)
                    }
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
                                val query: RealmQuery<*> = if (asset.parentType == Asset.ParentType.COMPLAINT) {
                                    realm.where(Complaint::class.java)
                                } else {
                                    realm.where(Confirmation::class.java)
                                }
                                @Suppress("UNCHECKED_CAST")
                                val result: HasFiles = (query as RealmQuery<HasFiles>)
                                        .equalTo("id", it.parentId)
                                        .findFirst()

                                val hasFiles = realm.copyFromRealm(result)
                                val files = hasFiles.files.map {
                                    if (it.localPath == asset.localPath) asset
                                    else it
                                }
                                hasFiles.files = RealmList(*files.toTypedArray())
                                realm.copyToRealmOrUpdate(hasFiles)
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

    override fun getComplaint(complaintId: String) =
            saguiService.getComplaint(complaintId)

    override fun getAssetsPendingUpload(): Observable<List<Asset>> {
        return Observable
                .merge(
                        pendingFilesFrom(Complaint::class.java),
                        pendingFilesFrom(Confirmation::class.java)
                )
                .flatMapIterable { it }
                .flatMapIterable { it.files }
                .filter { !it.sent }
                .toList()
                .toObservable()
    }

    private fun <T : HasFiles> pendingFilesFrom(clazz: Class<T>): Observable<List<HasFiles>> {
        return Observable.create<List<HasFiles>> { emitter ->
            Realm.getDefaultInstance().use { realm ->
                val result = realm.where(clazz)
                        .equalTo("files.sent", false)
                        .findAll()
                if (result != null) {
                    emitter.onNext(realm.copyFromRealm(result))
                }
                emitter.onComplete()
            }
        }
    }

    override fun confirmationFiles(confirmation: Confirmation): Observable<Confirmation> {
        return Observable.just(confirmation.files)
                .flatMapIterable { it }
                .map { asset ->
                    asset.parentId = confirmation.id!!
                    asset.parentType = Asset.ParentType.CONFIRMATION
                    asset
                }
                .flatMap { asset ->
                    Observable.create<Asset> { emitter ->
                        Realm.getDefaultInstance().use { realm ->
                            try {
                                realm.beginTransaction()
                                val files = confirmation.files.map {
                                    if (it.localPath == asset.localPath) asset
                                    else it
                                }
                                confirmation.files = RealmList(*files.toTypedArray())
                                realm.copyToRealmOrUpdate(confirmation)
                                realm.commitTransaction()
                                emitter.onNext(asset)
                                emitter.onComplete()
                            } catch (error: Exception) {
                                emitter.onError(error)
                            }
                        }
                    }
                }
                .toList()
                .toObservable()
                .flatMap { Observable.just(confirmation) }
    }

    override fun saveNotification(notification: Notification): Observable<Notification> {
        return Observable.create { emitter ->
            Realm.getDefaultInstance().use { realm ->
                try {
                    realm.beginTransaction()
                    if (notification.id == null) {
                        notification.id = UUID.randomUUID().toString()
                    }
                    realm.copyToRealmOrUpdate(notification)
                    realm.commitTransaction()
                    emitter.onNext(notification)
                    emitter.onComplete()
                } catch (error: Exception) {
                    emitter.onError(error)
                }
            }
        }
    }

    override fun listUnreadNotifications(): Observable<List<Notification>> {
        return Observable.create { emitter ->
            Realm.getDefaultInstance().use { realm ->
                val result = realm.where(Notification::class.java)
                        .equalTo("read", false)
                        .findAll()
                emitter.onNext(realm.copyFromRealm(result))
                emitter.onComplete()
            }
        }
    }

    override fun markAsRead(notification: Notification): Observable<Notification> {
        return Observable.create { emitter ->
            Realm.getDefaultInstance().use { realm ->
                try {
                    realm.beginTransaction()
                    val result = realm.where(Notification::class.java)
                            .equalTo("id", notification.id)
                            .findFirst()
                    result.read = true
                    realm.commitTransaction()
                    emitter.onNext(realm.copyFromRealm(result))
                    emitter.onComplete()
                } catch (error: Exception) {
                    emitter.onError(error)
                }
            }
        }
    }

    override fun listPendencies(): Observable<List<Pendency>> {
        return Observable
                .create<List<Complaint>> { emitter ->
                    Realm.getDefaultInstance().use { realm ->
                        try {
                            val results = realm.where(Complaint::class.java)
                                    .isNull("id")
                                    .findAll()
                            emitter.onNext(realm.copyFromRealm(results))
                            emitter.onComplete()
                        } catch (error: Exception) {
                            emitter.onError(error)
                        }
                    }
                }
                .flatMapIterable { it }
                .map { complaint ->
                    Pendency(id = complaint.pk,
                            message = "Você reportou um problema mas não informou o local.",
                            complaint = complaint)
                }
                .toList()
                .toObservable()
    }
}