package com.eokoe.sagui.data.entities

import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Pedro Silva
 * @since 30/08/17
 */
@PaperParcel
data class Data(
        val complaints: Int,
        val cases: Int,
        val actions: Int,
        val surveys: Int
) : PaperParcelable {
    companion object {
        @JvmField val CREATOR = PaperParcelData.CREATOR
    }
}