package com.eokoe.sagui

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.eokoe.sagui.data.AnswerListAdapter
import com.eokoe.sagui.data.net.ServiceGenerator
import com.eokoe.sagui.extensions.getManifestValue
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
        adapters = arrayOf(Adapter(AnswerListAdapter::class))
)
class SaguiApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initServiceGenerator()
        initRealm()
        Fresco.initialize(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    private fun initServiceGenerator() {
        val baseUrl = getManifestValue("apiBaseUrl")!!
        ServiceGenerator.init(this, baseUrl)
    }

    private fun initRealm() {
        Realm.init(this)
        val config = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded() // TODO define migration
                .build()
        Realm.setDefaultConfiguration(config)
    }
}
