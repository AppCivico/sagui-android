package com.eokoe.sagui.data.entities

import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Pedro Silva
 */
@PaperParcel
data class Survey(
        val id: Int,
        val title: String,
        val category: Int,
        val questions: List<Question>
) : PaperParcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelSurvey.CREATOR
    }
}