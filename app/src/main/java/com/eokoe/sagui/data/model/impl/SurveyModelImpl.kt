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
        Realm.getDefaultInstance().use { realm ->
            return try {
                realm.beginTransaction()
                val results = realm.where(Enterprise::class.java)
                        .equalTo("id", enterprise.id).or()
                        .equalTo("selected", true)
                        .findAll()
                results.map { it.selected = it.id == enterprise.id }
                realm.commitTransaction()
                enterprise.selected = true
                Observable.just(enterprise)
            } catch (error: Exception) {
                Observable.error(error)
            }
        }
    }

    override fun getSelectedEnterprise(): Observable<Enterprise> {
        Realm.getDefaultInstance().use { realm ->
            return try {
                val result = realm.where(Enterprise::class.java)
                        .equalTo("selected", true)
                        .findFirst()
                if (result != null && result.isValid) {
                    Observable.just(realm.copyFromRealm(result))
                } else {
                    Observable.empty()
                }
            } catch (error: Exception) {
                Observable.error(error)
            }
        }
    }

    override fun getEnterprises(): Observable<List<Enterprise>> {
        Realm.getDefaultInstance().use { realm ->
            val results = realm.where(Enterprise::class.java).findAll()
            if (results.isNotEmpty()) {
                val enterprises = realm.copyFromRealm(results)
                return Observable.just(enterprises)
            } else {
                return ServiceGenerator.getService(SurveyService::class.java).enterprises()
                        .map {
                            Realm.getDefaultInstance().use { realm ->
                                realm.beginTransaction()
                                realm.copyToRealmOrUpdate(it)
                                realm.commitTransaction()
                            }
                            return@map it
                        }
            }
        }
    }

    override fun getCategories(enterprise: Enterprise) =
            ServiceGenerator.getService(SurveyService::class.java).categories(enterprise.id)

    override fun getSurveyList(category: Category) =
            ServiceGenerator.getService(SurveyService::class.java).surveys(category.id)
}