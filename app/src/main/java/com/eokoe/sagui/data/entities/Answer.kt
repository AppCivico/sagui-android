package com.eokoe.sagui.data.entities

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Pedro Silva
 */
@PaperParcel
open class Answer(
        @SerializedName("question_id")
        var questionId: String? = null,
        @SerializedName("unit")
        var unitName: String? = null,
        @SerializedName("value", alternate = arrayOf("title"))
        var value: String = ""
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