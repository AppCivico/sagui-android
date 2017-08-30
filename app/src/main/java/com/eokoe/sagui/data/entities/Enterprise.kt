package com.eokoe.sagui.data.entities

import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Pedro Silva
 * @since 29/08/17
 */
@PaperParcel
data class Enterprise(
        val id: Int,
        val name: String,
        val description: String,
        val location: Location,
        val data: Data
) : PaperParcelable {
    companion object {
        @JvmField val CREATOR = PaperParcelEnterprise.CREATOR
    }
}