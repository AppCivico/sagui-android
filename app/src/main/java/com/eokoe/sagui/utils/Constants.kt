package com.eokoe.sagui.utils

import com.eokoe.sagui.BuildConfig

/**
 * @author Pedro Silva
 * @since 02/10/17
 */
val AUTHORITY = BuildConfig.APPLICATION_ID + ".fileprovider"

val IMAGE_PATH = "Pictures"

val VIDEO_PATH = "Movies"

val AUDIO_PATH = "Audio"

object Job {
    val UPLOAD_FILES = 1
    val UPLOAD_FILES_RETRY = 2
}