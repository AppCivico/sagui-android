package com.eokoe.sagui.di.modules

import com.eokoe.sagui.data.model.SaguiModel
import com.eokoe.sagui.data.model.impl.SaguiModelImpl
import com.eokoe.sagui.features.categories.CategoriesActivity
import com.eokoe.sagui.features.categories.CategoriesAdapter
import com.eokoe.sagui.features.complaints.ComplaintsActivity
import com.eokoe.sagui.features.complaints.ComplaintsContract
import com.eokoe.sagui.features.complaints.ComplaintsPresenter
import com.eokoe.sagui.features.complaints.details.ComplaintDetailsActivity
import com.eokoe.sagui.features.complaints.details.ComplaintDetailsAdapter
import com.eokoe.sagui.features.complaints.details.ConfirmContract
import com.eokoe.sagui.features.complaints.details.ConfirmPresenter
import com.eokoe.sagui.features.surveys.list.CategoriesContract
import com.eokoe.sagui.features.surveys.list.CategoriesPresenter
import org.koin.android.module.AndroidModule

/**
 * @author Pedro Silva
 * @since 09/11/17
 */
class AppModule : AndroidModule() {
    override fun context() = applicationContext {
        provide { SaguiModelImpl(get()) } bind SaguiModel::class

        context(CategoriesActivity.TAG) {
            provide { CategoriesPresenter(get()) } bind CategoriesContract.Presenter::class
            provide { CategoriesAdapter() }
        }

        context(ComplaintsActivity.TAG) {
            provide { ComplaintsPresenter(get()) } bind ComplaintsContract.Presenter::class
        }

        context(ComplaintDetailsActivity.TAG) {
            provide { ConfirmPresenter(get()) } bind ConfirmContract.Presenter::class
            provide { ComplaintDetailsAdapter() }
        }
    }
}