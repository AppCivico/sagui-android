package com.eokoe.sagui.data.entities

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
        open var location: Location = Location(),
        open var data: Data = Data(),
        open var selected: Boolean = false
) : PaperParcelable, RealmObject() {
    companion object {
        @JvmField val CREATOR = PaperParcelEnterprise.CREATOR
    }
}