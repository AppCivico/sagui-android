package com.eokoe.sagui.data.entities

import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Pedro Silva
 */
@PaperParcel
open class Pendency(
        var id: String,

        var typeStr: String = Type.COMPLAINT.name,

        var message: String = "",

        var complaint: Complaint
) : PaperParcelable {
    var type: Type
        get() = Type.valueOf(typeStr)
        set(value) {
            typeStr = value.name
        }

    companion object {
        @JvmField
        val CREATOR = PaperParcelPendency.CREATOR
    }

    enum class Type {
        COMPLAINT
    }
}