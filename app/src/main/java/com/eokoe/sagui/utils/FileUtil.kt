package com.eokoe.sagui.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 * @author Pedro Silva
 * @since 03/10/17
 */
object FileUtil {
    private val MAX_IMAGE_HEIGHT = 1080
    private val MAX_IMAGE_WIDTH = 1980

    fun compressImage(inputFilePath: String, outputFile: File) {
        val bitmap = BitmapFactory.decodeFile(inputFilePath)
        try {
            val exif = ExifInterface(inputFilePath)
            val orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION)
            val orientation = if (orientString != null) Integer.parseInt(orientString) else ExifInterface.ORIENTATION_NORMAL
            val rotationAngle = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
            saveImageFile(outputFile, bitmap, rotationAngle)
        } catch (error: IOException) {
            LogUtil.error(this, error)
        }
    }

    private fun saveImageFile(outputFile: File, bitmap: Bitmap, rotation: Float) {
        if (!outputFile.exists()) {
            val parent = File(outputFile.parent)
            if (!parent.exists()) {
                parent.mkdirs()
            }
            outputFile.createNewFile()
        }
        val fos = FileOutputStream(outputFile)
        var scale = 1f
        val scaledBitmap: Bitmap
        if (bitmap.width > bitmap.height) {
            if (MAX_IMAGE_WIDTH < bitmap.width) {
                scale = bitmap.width / MAX_IMAGE_WIDTH.toFloat()
            }
            scaledBitmap = if (scale > 1)
                Bitmap.createScaledBitmap(bitmap, MAX_IMAGE_WIDTH, (bitmap.height / scale).toInt(), true)
            else bitmap
        } else {
            if (MAX_IMAGE_HEIGHT < bitmap.height) {
                scale = bitmap.height / MAX_IMAGE_HEIGHT.toFloat()
            }
            scaledBitmap = if (scale > 1)
                Bitmap.createScaledBitmap(bitmap, (bitmap.width / scale).toInt(), MAX_IMAGE_HEIGHT, true)
            else bitmap
        }

        if (scaledBitmap != bitmap) {
            bitmap.recycle()
        }

        val matrix = Matrix()
        matrix.setRotate(rotation, scaledBitmap.width / 2f, scaledBitmap.height / 2f)
        val rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 82, fos)
        scaledBitmap.recycle()
        rotatedBitmap.recycle()
        fos.flush()
        fos.close()
    }
}