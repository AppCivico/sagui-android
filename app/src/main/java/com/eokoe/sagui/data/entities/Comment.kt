package com.eokoe.sagui.data.entities

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
        open var surveyId: String? = null,
        open var content: String = ""
) : PaperParcelable, RealmObject() {
    companion object {
        @JvmField
        val CREATOR = PaperParcelComment.CREATOR
    }
}