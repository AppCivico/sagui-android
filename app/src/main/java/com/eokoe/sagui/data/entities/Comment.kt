package com.eokoe.sagui.data.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Pedro Silva
 * @since 14/09/17
 */
@PaperParcel
open class Comment(
        open var id: String? = null,
        @SerializedName("submissions_id")
        @Expose(serialize = false)
        open var submissionsId: String? = null,
        open var content: String = ""
) : PaperParcelable, RealmObject() {
    companion object {
        @JvmField
        val CREATOR = PaperParcelComment.CREATOR
    }
}