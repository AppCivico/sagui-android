package com.eokoe.sagui.data.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Pedro Silva
 */
@PaperParcel
data class Survey(
        @Expose
        val id: String,

        @Expose
        val name: String,

        @Expose
        @SerializedName("axis")
        val categories: List<String>,

        @Expose
        val questions: List<Question>? = null,

        var hasAnswer: Boolean = false
) : PaperParcelable {

    companion object {
        @JvmField
        val CREATOR = PaperParcelSurvey.CREATOR
    }
}