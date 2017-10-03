package com.eokoe.sagui.data.entities

import android.net.Uri
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Pedro Silva
 */
@PaperParcel
open class Asset(
        var id: String? = null,
        @SerializedName("content_type")
        var type: String = "",
        var localPath: String = "",
        @SerializedName("path")
        var remotePath: String = "",
        var sent: Boolean = false
) : PaperParcelable, RealmObject() {
    var uri: Uri
        get() = Uri.parse(localPath)
        set(value) {
            localPath = value.path
        }

    companion object {
        @JvmField
        val CREATOR = PaperParcelAsset.CREATOR
    }
}