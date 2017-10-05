package com.eokoe.sagui.data.adapters

import android.os.Parcel
import com.eokoe.sagui.data.entities.Confirmation
import io.realm.RealmList
import paperparcel.TypeAdapter

/**
 * @author Pedro Silva
 * @since 13/09/17
 */
class ConfirmationListAdapter : TypeAdapter<RealmList<Confirmation>> {
    override fun readFromParcel(source: Parcel): RealmList<Confirmation> {
        val list: List<Confirmation> = ArrayList()
        source.readTypedList(list, Confirmation.CREATOR)
        return RealmList(*list.toTypedArray())
    }

    override fun writeToParcel(value: RealmList<Confirmation>, dest: Parcel, flags: Int) {
        dest.writeTypedList(value)
    }
}