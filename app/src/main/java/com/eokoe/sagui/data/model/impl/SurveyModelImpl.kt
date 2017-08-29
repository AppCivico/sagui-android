package com.eokoe.sagui.data.model.impl

import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.data.model.SurveyModel
import com.eokoe.sagui.data.net.ServiceGenerator
import com.eokoe.sagui.data.net.services.SurveyService

/**
 * @author Pedro Silva
 */
class SurveyModelImpl : SurveyModel {
    override fun getEnterprises() =
            ServiceGenerator.getService(SurveyService::class.java).enterprises()

    override fun getCategories() =
            ServiceGenerator.getService(SurveyService::class.java).categories()

    override fun getSurveyList(category: Category) =
            ServiceGenerator.getService(SurveyService::class.java).surveys(category.id)
}