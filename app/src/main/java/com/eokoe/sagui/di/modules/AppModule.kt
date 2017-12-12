package com.eokoe.sagui.di.modules

import android.os.Build
import com.eokoe.sagui.data.model.SaguiModel
import com.eokoe.sagui.data.model.impl.SaguiModelImpl
import com.eokoe.sagui.data.net.ServiceGenerator
import com.eokoe.sagui.data.net.services.SaguiService
import com.eokoe.sagui.features.categories.CategoriesActivity
import com.eokoe.sagui.features.categories.CategoriesAdapter
import com.eokoe.sagui.features.complaints.ComplaintsActivity
import com.eokoe.sagui.features.complaints.ComplaintsContract
import com.eokoe.sagui.features.complaints.ComplaintsPresenter
import com.eokoe.sagui.features.complaints.details.ComplaintDetailsActivity
import com.eokoe.sagui.features.complaints.details.ComplaintDetailsAdapter
import com.eokoe.sagui.features.complaints.details.ConfirmContract
import com.eokoe.sagui.features.complaints.details.ConfirmPresenter
import com.eokoe.sagui.features.complaints.report.ReportActivity
import com.eokoe.sagui.features.complaints.report.ReportAdapter
import com.eokoe.sagui.features.complaints.report.ReportContract
import com.eokoe.sagui.features.complaints.report.ReportPresenter
import com.eokoe.sagui.features.enterprises.EnterprisesActivity
import com.eokoe.sagui.features.enterprises.EnterprisesAdapter
import com.eokoe.sagui.features.enterprises.EnterprisesContract
import com.eokoe.sagui.features.enterprises.EnterprisesPresenter
import com.eokoe.sagui.features.help.HelpActivity
import com.eokoe.sagui.features.help.HelpAdapter
import com.eokoe.sagui.features.notifications.NotificationContract
import com.eokoe.sagui.features.notifications.NotificationsActivity
import com.eokoe.sagui.features.notifications.NotificationsAdapter
import com.eokoe.sagui.features.notifications.NotificationsPresenter
import com.eokoe.sagui.features.pendencies.PendenciesActivity
import com.eokoe.sagui.features.pendencies.PendenciesAdapter
import com.eokoe.sagui.features.pendencies.PendenciesContract
import com.eokoe.sagui.features.pendencies.PendenciesPresenter
import com.eokoe.sagui.features.splash.SplashActivity
import com.eokoe.sagui.features.splash.SplashContract
import com.eokoe.sagui.features.splash.SplashPresenter
import com.eokoe.sagui.features.surveys.list.*
import com.eokoe.sagui.features.surveys.survey.SurveyActivity
import com.eokoe.sagui.features.surveys.survey.SurveyContract
import com.eokoe.sagui.features.surveys.survey.SurveyPresenter
import com.eokoe.sagui.features.surveys.survey.note.NoteActivity
import com.eokoe.sagui.features.surveys.survey.note.NoteContract
import com.eokoe.sagui.features.surveys.survey.note.NotePresenter
import com.eokoe.sagui.services.upload.UploadFilesJobIntentService
import com.eokoe.sagui.services.upload.UploadFilesRetry
import com.eokoe.sagui.services.upload.UploadFilesRetryDefault
import com.eokoe.sagui.services.upload.UploadFilesRetryLollipop
import org.koin.android.module.AndroidModule

/**
 * @author Pedro Silva
 * @since 09/11/17
 */
class AppModule : AndroidModule() {
    override fun context() = applicationContext {
        provide { SaguiModelImpl(get(), get()) } bind SaguiModel::class
        provide { ServiceGenerator.getService<SaguiService>() } bind SaguiService::class

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

        context(ReportActivity.TAG) {
            provide { ReportPresenter(get()) } bind ReportContract.Presenter::class
            provide { ReportAdapter() }
        }

        context(EnterprisesActivity.TAG) {
            provide { EnterprisesPresenter(get()) } bind EnterprisesContract.Presenter::class
            provide { EnterprisesAdapter() }
        }

        context(HelpActivity.TAG) {
            provide { HelpAdapter() }
        }

        context(NotificationsActivity.TAG) {
            provide { NotificationsPresenter(get()) } bind NotificationContract.Presenter::class
            provide { NotificationsAdapter() }
        }

        context(PendenciesActivity.TAG) {
            provide { PendenciesPresenter(get()) } bind PendenciesContract.Presenter::class
            provide { PendenciesAdapter() }
        }

        context(SplashActivity.TAG) {
            provide { SplashPresenter(get()) } bind SplashContract.Presenter::class
        }

        context(SurveyListActivity.TAG) {
            provide { SurveyListPresenter(get()) } bind SurveyListContract.Presenter::class
            provide { SurveyListAdapter() }
        }

        context(NoteActivity.TAG) {
            provide { NotePresenter(get()) } bind NoteContract.Presenter::class
        }

        context(SurveyActivity.TAG) {
            provide { SurveyPresenter(get()) } bind SurveyContract.Presenter::class
        }

        context(UploadFilesJobIntentService.TAG) {
            provide {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    UploadFilesRetryLollipop()
                } else {
                    UploadFilesRetryDefault()
                }
            } bind UploadFilesRetry::class
        }
    }
}