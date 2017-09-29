package com.eokoe.sagui.data.entities

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * @author Pedro Silva
 */
open class Confirmation(
        var id: String? = null,
        @SerializedName("complaint_id")
        @PrimaryKey
        var complaintId: String = ""
) : RealmObject()