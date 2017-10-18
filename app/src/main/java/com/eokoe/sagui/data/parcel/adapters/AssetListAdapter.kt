package com.eokoe.sagui.data.parcel.adapters

import android.os.Parcel
import com.eokoe.sagui.data.entities.Asset
import io.realm.RealmList
import paperparcel.TypeAdapter

/**
 * @author Pedro Silva
 * @since 13/09/17
 */
class AssetListAdapter : TypeAdapter<RealmList<Asset>> {
    override fun readFromParcel(source: Parcel): RealmList<Asset> {
        val list = ArrayList<Asset>()
        source.readTypedList(list, Asset.CREATOR)
        return RealmList(*list.toTypedArray())
    }

    override fun writeToParcel(value: RealmList<Asset>, dest: Parcel, flags: Int) {
        dest.writeTypedList(value)
    }
}