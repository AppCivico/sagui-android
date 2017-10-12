package com.eokoe.sagui.data.entities

import android.net.Uri
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import paperparcel.PaperParcel
import paperparcel.PaperParcelable
import java.io.File

/**
 * @author Pedro Silva
 */
@PaperParcel
open class Asset(
        @Expose
        var id: String? = null,

        var parentId: String = "",

        var parentTypeStr: String = "",

        @Expose
        @SerializedName("content_type")
        var type: String? = null,

        var localPath: String? = null,

        @Expose
        @SerializedName("path")
        var remotePath: String? = null,

        var sent: Boolean = false
) : PaperParcelable, RealmObject() {

    var parentType: ParentType
        get() = ParentType.valueOf(parentTypeStr)
        set(value) {
            parentTypeStr = value.name
        }

    var uri: Uri
        get() =
            if (isLocal) Uri.parse(localPath)
            else Uri.parse(remotePath)
        set(value) {
            localPath = value.toString()
        }

    val isLocal: Boolean
        get() = localPath != null && File(localPath).exists()

    val isImage: Boolean
        get() = type?.matches("image/.+".toRegex()) == true

    val isVideo: Boolean
        get() = type?.matches("video/.+".toRegex()) == true

    val isAudio: Boolean
        get() = type?.matches("audio/.+".toRegex()) == true

    companion object {
        @JvmField
        val CREATOR = PaperParcelAsset.CREATOR
    }

    enum class ParentType {
        COMPLAINT, CONFIRMATION
    }
}