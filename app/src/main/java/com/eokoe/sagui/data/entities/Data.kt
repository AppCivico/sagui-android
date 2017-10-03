package com.eokoe.sagui.data.entities

import com.google.gson.annotations.Expose
import io.realm.RealmObject
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Pedro Silva
 * @since 30/08/17
 */
@PaperParcel
open class Data(
        @Expose
        open var complaints: Int = 0,

        @Expose
        open var cases: Int = 0,

        @Expose
        open var actions: Int = 0,

        @Expose
        open var surveys: Int = 0
) : PaperParcelable, RealmObject() {

    companion object {
        @JvmField
        val CREATOR = PaperParcelData.CREATOR
    }
}