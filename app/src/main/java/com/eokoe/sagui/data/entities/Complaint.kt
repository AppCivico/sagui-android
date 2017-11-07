package com.eokoe.sagui.data.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
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
        var pk: String = "",

        @Expose
        var id: String? = null,

        @Expose
        var title: String = "",

        @Expose
        var description: String = "",

        @Expose
        @SerializedName("axis_id")
        var categoryId: String? = null,

        @Expose
        @SerializedName("axis")
        var category: Category? = null,

        @Expose
        var location: LatLong? = null,

        @Expose
        @SerializedName("human_address")
        var address: String? = null,

        @Expose
        @SerializedName("enterprise_id")
        var enterpriseId: String? = null,

        @Expose(serialize = false, deserialize = true)
        @SerializedName("enterprise")
        var enterprise: Enterprise? = null,

        @Expose(serialize = false, deserialize = true)
        @SerializedName("confirmations")
        var confirmationsList: RealmList<Confirmation> = RealmList(),

        @Expose(serialize = false, deserialize = true)
        override var files: RealmList<Asset> = RealmList(),

        @Expose(serialize = false, deserialize = true)
        @SerializedName("is_cause")
        var isCause: Boolean = false,

        @Expose(serialize = false, deserialize = true)
        @SerializedName("num_to_became_cause")
        var numToBecameCause: Int = 99,

        @Expose(serialize = false, deserialize = true)
        var comments: RealmList<Comment> = RealmList()
) : PaperParcelable, RealmObject(), HasFiles {
    val confirmations: Int
        get() = confirmationsList.size

    companion object {
        @JvmField
        val CREATOR = PaperParcelComplaint.CREATOR
    }
}