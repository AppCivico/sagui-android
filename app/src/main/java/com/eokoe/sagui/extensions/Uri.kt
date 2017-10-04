package com.eokoe.sagui.extensions

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.eokoe.sagui.utils.AUTHORITY
import java.io.File

/**
 * @author Pedro Silva
 * @since 02/10/17
 */


fun Uri.toFile(context: Context): File? {
    val path = getRealPath(context) ?: path
    return if (isLocal(path)) {
        return File(path)
    } else null
}

fun Uri.getRealPath(context: Context): String? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, this)) {
        val docId = DocumentsContract.getDocumentId(this)
        if (isLocalStorageDocument) {
            return docId
        } else if (isExternalStorageDocument) {
            val split = docId.split(":")
            val type = split[0]
            if ("primary".equals(type, true)) {
                return Environment.getExternalStorageDirectory().absolutePath + "/" + split[1]
            }
            // TODO handle non-primary volumes
        } else if (isDownloadsDocument) {
            val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), docId.toLong())
            return contentUri.getDataColumn(context)
        } else if (isMediaDocument) {
            val split = docId.split(":")
            val type = split[0]

            val contentUri: Uri? = when (type) {
                "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                else -> null
            }
            val selection = "_id=?"
            return contentUri?.getDataColumn(context, selection, split[1])
        }
    } else if ("content".equals(scheme, true)) {
        if (isGooglePhotosUri) return lastPathSegment
        return getDataColumn(context)
    } else if ("file".equals(scheme, true)) {
        return path
    }
    return null
}

val Uri.isLocalStorageDocument: Boolean
    get() = AUTHORITY == this.authority

val Uri.isExternalStorageDocument: Boolean
    get() = "com.android.externalstorage.documents" == authority

val Uri.isDownloadsDocument: Boolean
    get() = "com.android.providers.downloads.documents" == authority

val Uri.isMediaDocument: Boolean
    get() = "com.android.providers.media.documents" == authority

val Uri.isGooglePhotosUri: Boolean
    get() = authority?.startsWith("com.google.android.apps.photos.content") == true

fun Uri.isLocal(path: String?) = path != null && !path.matches("https?://.+".toRegex())

fun Uri.getDataColumn(context: Context, selection: String? = null, vararg selectionArgs: String): String? {
    val column = "_data"
    val projection = arrayOf(column)
    return context.contentResolver
            .query(this, projection, selection, selectionArgs, null)
            ?.use { cursor ->
                try {
                    val columnIndex = cursor.getColumnIndexOrThrow(column)
                    cursor.getString(columnIndex)
                } catch (error: Exception) {
                    error.printStackTrace()
                    null
                }
            }
}

fun Uri.getMimeType(context: Context): String {
    return if (scheme == ContentResolver.SCHEME_CONTENT) {
        context.contentResolver.getType(this)
    } else {
        val extension = MimeTypeMap.getFileExtensionFromUrl(this.toString())
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase())
    }
}