package com.eokoe.sagui.data.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Pedro Silva
 */
@PaperParcel
open class Confirmation(
        @Expose
        var id: String? = null,

        @Expose
        @PrimaryKey
        @SerializedName("complaint_id")
        var complaintId: String = ""
) : PaperParcelable, RealmObject() {

    companion object {
        @JvmField
        val CREATOR = PaperParcelConfirmation.CREATOR
    }
}