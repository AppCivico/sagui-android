package com.eokoe.sagui.data.parcel.adapters

import android.os.Parcel
import com.eokoe.sagui.data.entities.Comment
import io.realm.RealmList
import paperparcel.TypeAdapter

/**
 * @author Pedro Silva
 * @since 13/09/17
 */
class CommentListAdapter : TypeAdapter<RealmList<Comment>> {
    override fun readFromParcel(source: Parcel): RealmList<Comment> {
        val list = ArrayList<Comment>()
        source.readTypedList(list, Comment.CREATOR)
        return RealmList(*list.toTypedArray())
    }

    override fun writeToParcel(value: RealmList<Comment>, dest: Parcel, flags: Int) {
        dest.writeTypedList(value)
    }
}