package com.eokoe.sagui.data.entities

import com.google.gson.annotations.Expose
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

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
        open var content: String = ""
) : PaperParcelable {

    companion object {
        @JvmField
        val CREATOR = PaperParcelComment.CREATOR
    }
}