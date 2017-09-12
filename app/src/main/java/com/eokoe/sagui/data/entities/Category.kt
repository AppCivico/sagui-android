package com.eokoe.sagui.data.entities

import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Pedro Silva
 * @since 16/08/17
 */
@PaperParcel
data class Category(
        var id: String,
        var name: String
) : PaperParcelable {
    companion object {
        @JvmField val CREATOR = PaperParcelCategory.CREATOR
    }
}