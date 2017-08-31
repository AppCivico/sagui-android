package com.eokoe.sagui.data.entities

import io.realm.RealmObject
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Pedro Silva
 * @since 30/08/17
 */
@PaperParcel
open class LatLong(
        open var latitude: Double = 0.0,
        open var longitude: Double = 0.0
) : PaperParcelable, RealmObject() {
    companion object {
        @JvmField
        val CREATOR = PaperParcelLatLong.CREATOR
    }
}