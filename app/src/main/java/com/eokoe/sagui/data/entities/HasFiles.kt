package com.eokoe.sagui.data.entities

import io.realm.RealmList
import io.realm.RealmModel

/**
 * @author Pedro Silva
 */
interface HasFiles: RealmModel {
    var files: RealmList<Asset>
}