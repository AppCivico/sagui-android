package com.eokoe.sagui.data.entities

import io.realm.RealmObject
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Pedro Silva
 */
@PaperParcel
open class Pendency(
        var message: String = ""
) : PaperParcelable, RealmObject() {
    companion object {
        @JvmField
        val CREATOR = PaperParcelPendency.CREATOR
    }
}