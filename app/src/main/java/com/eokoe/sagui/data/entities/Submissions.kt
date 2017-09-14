package com.eokoe.sagui.data.entities

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * @author Pedro Silva
 * @since 13/09/17
 */
@PaperParcel
open class Submissions(
        var id: String? = null,
        @SerializedName("survey_id")
        var surveyId: String? = null,
        var location: LatLong? = null,
        var answers: RealmList<Answer> = RealmList()
) : PaperParcelable, RealmObject() {
    companion object {
        @JvmField
        val CREATOR = PaperParcelSubmissions.CREATOR
    }
}