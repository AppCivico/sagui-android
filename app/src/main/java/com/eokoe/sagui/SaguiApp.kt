package com.eokoe.sagui

import android.app.Application
import com.eokoe.sagui.data.net.ServiceGenerator
import com.eokoe.sagui.extensions.getManifestValue
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * @author Pedro Silva
 * @since 14/08/17
 */

class SaguiApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initServiceGenerator()
        initRealm()
    }

    private fun initServiceGenerator() {
        val baseUrl = getManifestValue("apiBaseUrl")!!
        ServiceGenerator.init(baseUrl)
    }

    private fun initRealm() {
        Realm.init(this)
        val config = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded() // TODO define migration
                .build()
        Realm.setDefaultConfiguration(config)
    }
}
