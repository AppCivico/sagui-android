package com.eokoe.sagui.data.entities

import com.google.gson.annotations.Expose
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
        @Expose
        var id: String? = null,

        @Expose
        @SerializedName("survey_id")
        var surveyId: String? = null,

        @Expose
        var location: LatLong? = null,

        @Expose
        var answers: RealmList<Answer> = RealmList()
) : PaperParcelable, RealmObject() {

    companion object {
        @JvmField
        val CREATOR = PaperParcelSubmissions.CREATOR
    }
}