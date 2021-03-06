package com.eokoe.sagui.data.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Pedro Silva
 */
@PaperParcel
open class Answer(
        @Expose
        @SerializedName("question_id")
        open var questionId: String? = null,

        @Expose
        @SerializedName("unit")
        open var unitName: String? = null,

        @Expose
        @SerializedName("value", alternate = arrayOf("title"))
        open var value: String = "",

        @Expose
        @SerializedName("image_path")
        open var image: String? = null
) : PaperParcelable, RealmObject() {

    var unit: Unit?
        get() = if (unitName != null) Unit.valueOf(unitName!!.toUpperCase()) else null
        set(value) {
            unitName = value?.name
        }

    enum class Unit {
        @SerializedName("red")
        RED,
        @SerializedName("yellow")
        YELLOW,
        @SerializedName("green")
        GREEN
    }

    companion object {
        @JvmField
        val CREATOR = PaperParcelAnswer.CREATOR
    }
}