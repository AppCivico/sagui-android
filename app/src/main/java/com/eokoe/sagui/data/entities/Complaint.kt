package com.eokoe.sagui.data.entities

/**
 * @author Pedro Silva
 * @since 25/09/17
 */
class Complaint(
        var id: String? = null,
        val title: String = "",
        val description: String = "",
        val location: LatLong = LatLong()
)