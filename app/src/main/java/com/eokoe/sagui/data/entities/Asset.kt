package com.eokoe.sagui.data.entities

import android.net.Uri
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Pedro Silva
 */
@PaperParcel
open class Asset(
        @Expose
        var id: String? = null,

        var complaintId: String = "",

        @Expose
        @SerializedName("content_type")
        var type: String = "",

        var localPath: String? = null,

        @Expose
        @SerializedName("path")
        var remotePath: String? = null,

        var sent: Boolean = false
) : PaperParcelable, RealmObject() {

    constructor(uri: Uri?) : this(localPath = uri?.toString())

    var uri: Uri
        get() = Uri.parse(localPath)
        set(value) {
            localPath = value.toString()
        }

    companion object {
        @JvmField
        val CREATOR = PaperParcelAsset.CREATOR
    }
}