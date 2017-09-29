package com.eokoe.sagui.data.entities

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Pedro Silva
 * @since 16/08/17
 */
@PaperParcel
open class Category(
        var id: String = "",
        var name: String = "",
        @SerializedName("icon_code")
        var iconCode: String? = null
) : PaperParcelable, RealmObject() {

    val symbol: String?
        get() {
            if (iconCode != null) {
                return String(Character.toChars(Integer.parseInt(iconCode, 16)))
            }
            return null
        }

    companion object {
        @JvmField
        val CREATOR = PaperParcelCategory.CREATOR
    }
}