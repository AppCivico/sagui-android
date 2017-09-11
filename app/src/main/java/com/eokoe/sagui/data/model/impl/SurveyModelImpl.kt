package com.eokoe.sagui.data.model.impl

import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.data.model.SurveyModel
import com.eokoe.sagui.data.net.ServiceGenerator
import com.eokoe.sagui.data.net.services.SurveyService
import io.reactivex.Observable
import io.realm.Realm

/**
 * @author Pedro Silva
 */
class SurveyModelImpl : SurveyModel {
    override fun selectEnterprise(enterprise: Enterprise): Observable<Enterprise> {
        return Observable.create { emitter ->
            Realm.getDefaultInstance().use { realm ->
                try {
                    realm.beginTransaction()
                    realm.where(Enterprise::class.java)
                            .equalTo("id", enterprise.id).or()
                            .equalTo("selected", true)
                            .findAll()
                            .map { it.selected = it.id == enterprise.id }
                    realm.commitTransaction()
                    enterprise.selected = true
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
            ServiceGenerator.getService(SurveyService::class.java).enterprises()

    override fun getCategories(enterprise: Enterprise) =
            ServiceGenerator.getService(SurveyService::class.java).categories(enterprise.id)

    override fun getSurveyList(category: Category) =
            ServiceGenerator.getService(SurveyService::class.java).surveys(category.id)
}