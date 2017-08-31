package com.eokoe.sagui

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * @author Pedro Silva
 * @since 14/08/17
 */

class SaguiApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initRealm()
    }

    private fun initRealm() {
        Realm.init(this)
        val config = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded() // TODO define migration
                .build()
        Realm.setDefaultConfiguration(config)
    }
}
