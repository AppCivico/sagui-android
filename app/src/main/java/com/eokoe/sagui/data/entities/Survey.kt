package com.eokoe.sagui.data.entities

import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Pedro Silva
 */
@PaperParcel
data class Survey(
        val id: String,
        val name: String,
        @SerializedName("axis")
        val categories: List<String>,
        val questions: List<Question>? = null
) : PaperParcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelSurvey.CREATOR
    }
}