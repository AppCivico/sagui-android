package com.eokoe.sagui.data.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import paperparcel.PaperParcel
import paperparcel.PaperParcelable
import java.util.*

/**
 * @author Pedro Silva
 * @since 14/09/17
 */
@PaperParcel
open class Comment(
        @Expose
        open var id: String? = null,

        open var submissionsId: String? = null,

        @Expose
        open var content: String = "",

        @Expose(serialize = false, deserialize = true)
        @SerializedName("created_at")
        var createdAt: Date? = null
) : RealmObject(), PaperParcelable {

    companion object {
        @JvmField
        val CREATOR = PaperParcelComment.CREATOR
    }
}