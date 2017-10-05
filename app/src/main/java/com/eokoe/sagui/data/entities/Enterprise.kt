package com.eokoe.sagui.data.entities

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.maps.android.PolyUtil
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Pedro Silva
 * @since 29/08/17
 */
@PaperParcel
open class Enterprise(
        @Expose
        @PrimaryKey
        open var id: String = "",

        @Expose
        open var name: String = "",

        @Expose
        open var description: String? = null,

        @Expose
        @SerializedName("human_address")
        open var address: String = "",

        @Expose
        @SerializedName("location")
        open var locationEncoded: String? = null,

        @Expose
        open var data: Data = Data(),

        open var selected: Boolean = false,

        @Expose
        open var images: RealmList<Image> = RealmList()
) : PaperParcelable, RealmObject() {

    val location: List<LatLng>?
        get() {
            return if (locationEncoded != null) {
                PolyUtil.decode(locationEncoded)
            } else null
        }

    companion object {
        @JvmField
        val CREATOR = PaperParcelEnterprise.CREATOR
    }
}