package com.eokoe.sagui.data.entities

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import com.google.maps.android.PolyUtil
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
        @PrimaryKey
        open var id: String = "",
        open var name: String = "",
        open var description: String? = null,
        @SerializedName("human_address")
        open var address: String = "",
        @SerializedName("location")
        open var locationEncoded: String? = null,
        open var data: Data = Data(),
        open var selected: Boolean = false
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