package com.eokoe.sagui.data.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Pedro Silva
 */
@PaperParcel
data class Question(
        @Expose
        val id: String,

        @Expose
        val name: String,

        @Expose
        val type: Type,

        @Expose
        val answers: List<Answer>? = null
) : PaperParcelable {

    enum class Type {
        @SerializedName("textarea")
        TEXT,
        @SerializedName("traffic_light")
        TRAFFIC_LIGHT,
        @SerializedName("multiple")
        MULTIPLE
    }

    companion object {
        @JvmField
        val CREATOR = PaperParcelQuestion.CREATOR
    }
}