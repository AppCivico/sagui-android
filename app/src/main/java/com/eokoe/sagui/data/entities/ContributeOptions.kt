package com.eokoe.sagui.data.entities

import android.content.Context
import android.support.annotation.StringRes
import com.eokoe.sagui.R

enum class ContributeOptions(
        @StringRes
        val text: Int
) {
    TAKE_PICTURE(R.string.take_picture),
    GALLERY_PICTURE(R.string.select_picture_from_gallery),
    RECORD_VIDEO(R.string.record_video),
    GALLERY_VIDEO(R.string.select_video_from_gallery),
    AUDIO(R.string.insert_audio);

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