package com.eokoe.sagui.data.parcel.adapters

import android.os.Parcel
import com.eokoe.sagui.data.entities.Image
import io.realm.RealmList
import paperparcel.TypeAdapter

/**
 * @author Pedro Silva
 * @since 13/09/17
 */
class ImageListAdapter : TypeAdapter<RealmList<Image>> {
    override fun readFromParcel(source: Parcel): RealmList<Image> {
        val list: List<Image> = ArrayList()
        source.readTypedList(list, Image.CREATOR)
        return RealmList(*list.toTypedArray())
    }

    override fun writeToParcel(value: RealmList<Image>, dest: Parcel, flags: Int) {
        dest.writeTypedList(value)
    }
}