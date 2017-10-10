package com.eokoe.sagui.data.entities

import android.content.Context
import com.eokoe.sagui.R

enum class ContributeOptions(val text: Int) {
    TAKE_PICTURE(R.string.take_picture),
    GALLERY_PICTURE(R.string.select_picture_from_gallery);

    fun string(context: Context): String = context.getString(text)

    companion object {
        fun list(context: Context): Array<String> {
            return values().map { option ->
                option.string(context)
            }.toTypedArray()
        }

        fun fromPosition(position: Int) =
                values().first { it.ordinal == position }
    }
}