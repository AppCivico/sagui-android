package com.eokoe.sagui.utils

/**
 * @author Pedro Silva
 * @since 02/10/17
 */

val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"

object Job {
    val UPLOAD_FILES = 1
    val UPLOAD_FILES_RETRY = 2
}

object Files {
    object Path {
        val IMAGE_PATH = "Pictures"
        val VIDEO_PATH = "Movies"
        val AUDIO_PATH = "Audio"
    }

    object Extensions {
        val MP4 = ".mp4"
        val JPG = ".jpg"
        val AAC = ".aac"
        val NONE = ""
    }
}

object RequestCode {
    enum class Intent {
        PREVIEW_ASSET, LOCATION,
        GALLERY_PICTURE, GALLERY_VIDEO,
        CAMERA, CAMERA_PICTURE, CAMERA_VIDEO,
        AUDIO;

        val value: Int
            get() = ordinal

        companion object {
            fun fromInt(position: Int) =
                    values().firstOrNull { it.value == position }
        }

    }

    enum class Permission {
        READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE,
        PICTURE_STORAGE, VIDEO_STORAGE,
        CAMERA, CAMERA_PICTURE, CAMERA_VIDEO,
        AUDIO;

        val value: Int
            get() = ordinal

        companion object {
            fun fromInt(position: Int) =
                    values().firstOrNull { it.value == position }
        }
    }
}