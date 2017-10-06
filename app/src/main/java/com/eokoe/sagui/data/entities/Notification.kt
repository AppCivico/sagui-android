package com.eokoe.sagui.data.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import paperparcel.PaperParcel
import paperparcel.PaperParcelable
import java.util.*

/**
 * @author Pedro Silva
 * @since 06/10/17
 */
@PaperParcel
open class Notification(
        @Expose
        @SerializedName("event")
        var eventStr: String = "",

        @Expose
        @SerializedName("type")
        var typeStr: String = "",

        @Expose
        @PrimaryKey
        var id: String = "",

        @Expose
        var message: String = "",

        @Expose
        @SerializedName("created_at")
        var createdAt: Date = Date(),

        var read: Boolean = false
) : PaperParcelable, RealmObject() {
    val event: Event
        get() = Event.valueOf(eventStr)

    val type: Type
        get() = Type.valueOf(typeStr)

    enum class Event {
        CAUSE, COMMENT, CONFIRMATION
    }

    enum class Type {
        COMPLAINT
    }

    companion object {
        @JvmField
        val CREATOR = PaperParcelNotification.CREATOR
    }
}