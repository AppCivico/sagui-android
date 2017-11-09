package com.eokoe.sagui

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.eokoe.sagui.data.net.ServiceGenerator
import com.eokoe.sagui.data.parcel.adapters.AnswerListAdapter
import com.eokoe.sagui.data.parcel.adapters.AssetListAdapter
import com.eokoe.sagui.data.parcel.adapters.CommentListAdapter
import com.eokoe.sagui.data.parcel.adapters.ConfirmationListAdapter
import com.eokoe.sagui.di.AppInjector
import com.facebook.drawee.backends.pipeline.Fresco
import io.realm.Realm
import io.realm.RealmConfiguration
import paperparcel.Adapter
import paperparcel.ProcessorConfig

/**
 * @author Pedro Silva
 * @since 14/08/17
 */
@ProcessorConfig(
        adapters = arrayOf(
                Adapter(AnswerListAdapter::class),
                Adapter(AssetListAdapter::class),
                Adapter(ConfirmationListAdapter::class),
                Adapter(CommentListAdapter::class)
        )
)
class SaguiApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initServiceGenerator()
        initRealm()
        Fresco.initialize(this)
        AppInjector.init(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    private fun initServiceGenerator() {
        ServiceGenerator.init(this, BuildConfig.API_BASE_URL)
    }

    private fun initRealm() {
        Realm.init(this)
        val config = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded() // TODO define migration
                .build()
        Realm.setDefaultConfiguration(config)
    }
}
