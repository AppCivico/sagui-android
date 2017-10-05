package com.eokoe.sagui.data.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Pedro Silva
 * @since 05/10/17
 */
@PaperParcel
open class Image(
        @Expose
        @SerializedName("image_path")
        var imagePath: String = ""
) : PaperParcelable, RealmObject() {
    companion object {
        @JvmField
        val CREATOR = PaperParcelImage.CREATOR
    }
}