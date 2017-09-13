package com.eokoe.sagui.data.entities

import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Pedro Silva
 */
@PaperParcel
data class Answer(
        val unit: Unit? = null,
        val title: String
) : PaperParcelable {
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