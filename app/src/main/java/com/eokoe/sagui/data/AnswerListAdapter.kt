package com.eokoe.sagui.data

import android.os.Parcel
import com.eokoe.sagui.data.entities.Answer
import io.realm.RealmList
import paperparcel.TypeAdapter

/**
 * @author Pedro Silva
 * @since 13/09/17
 */
class AnswerListAdapter : TypeAdapter<RealmList<Answer>> {
    override fun readFromParcel(source: Parcel): RealmList<Answer> {
        val list: List<Answer> = ArrayList()
        source.readTypedList(list, Answer.CREATOR)
        return RealmList(*list.toTypedArray())
    }

    override fun writeToParcel(value: RealmList<Answer>, dest: Parcel, flags: Int) {
        dest.writeTypedList(value)
    }
}