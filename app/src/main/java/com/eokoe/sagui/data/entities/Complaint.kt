package com.eokoe.sagui.data.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Pedro Silva
 * @since 25/09/17
 */
@PaperParcel
open class Complaint(
        @PrimaryKey
        var id: String? = null,
        var title: String = "",
        var description: String = "",
        @SerializedName("axis_id")
        var categoryId: String? = null,
        @SerializedName("axis")
        var category: Category? = null,
        var location: LatLong? = null,
        @SerializedName("human_address")
        var address: String? = null,
        @SerializedName("enterprise_id")
        var enterpriseId: String? = null,
        @SerializedName("confirmations")
        var confirmationsList: RealmList<Confirmation>? = null,
        @Expose(serialize = false)
        var files: RealmList<Asset> = RealmList()
) : PaperParcelable, RealmObject() {
    val confirmations: Int
        get() = confirmationsList?.size ?: 0

    companion object {
        @JvmField
        val CREATOR = PaperParcelComplaint.CREATOR
    }
}