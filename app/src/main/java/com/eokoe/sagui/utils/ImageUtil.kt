package com.eokoe.sagui.utils

import android.content.Context
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import com.eokoe.sagui.extensions.getRealPath
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException



/**
 * @author Pedro Silva
 * @since 03/10/17
 */
object ImageUtil {
    private val MAX_HEIGHT = 1080
    private val MAX_WIDTH = 1980

    fun compressImage(context: Context, inputFile: Uri, outputFile: File) {
        val bitmap = BitmapFactory.decodeFile(inputFile.getRealPath(context))
        return compressImage(context, bitmap, outputFile)
    }

    fun compressImage(context: Context, bitmap: Bitmap, outputFile: File) {
        val fos = FileOutputStream(outputFile)
        var scale = 1f
        val scaledBitmap: Bitmap
        if (bitmap.width > bitmap.height) {
            if (MAX_WIDTH < bitmap.width) {
                scale = bitmap.width / MAX_WIDTH.toFloat()
            }
            scaledBitmap = if (scale > 1)
                Bitmap.createScaledBitmap(bitmap, MAX_WIDTH, (bitmap.height / scale).toInt(), true)
            else bitmap
        } else {
            if (MAX_HEIGHT < bitmap.height) {
                scale = bitmap.height / MAX_HEIGHT.toFloat()
            }
            scaledBitmap = if (scale > 1)
                Bitmap.createScaledBitmap(bitmap, (bitmap.width / scale).toInt(), MAX_HEIGHT, true)
            else bitmap
        }

        if (scaledBitmap != bitmap) {
            bitmap.recycle()
        }
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 82, fos)
        scaledBitmap.recycle()
        fos.flush()
        fos.close()
    }

    fun compressImageOld(context: Context, inputFile: Uri, outputFile: File): Uri? {
        val filePath = inputFile.getRealPath(context)
        var scaledBitmap: Bitmap? = null
        val options = BitmapFactory.Options()
        // by setting this field as true, the actual bitmap pixels are not loaded in the memory.
        // Just the bounds are loaded. If you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true
        var bmp = BitmapFactory.decodeFile(filePath, options)
        var actualHeight = options.outHeight
        var actualWidth = options.outWidth
        var imgRatio = actualWidth / actualHeight
        val maxRatio = MAX_WIDTH / MAX_HEIGHT
        //width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > MAX_HEIGHT || actualWidth > MAX_WIDTH) {
            when {
                imgRatio < maxRatio -> {
                    imgRatio = MAX_HEIGHT / actualHeight
                    actualWidth = (imgRatio * actualWidth.toFloat()).toInt()
                    actualHeight = MAX_HEIGHT
                }
                imgRatio > maxRatio -> {
                    imgRatio = MAX_WIDTH / actualWidth
                    actualHeight *= imgRatio
                    actualWidth = MAX_WIDTH
                }
                else -> {
                    actualHeight = MAX_HEIGHT
                    actualWidth = MAX_WIDTH
                }
            }
        }

        //setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)

        //inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false
        //this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)
        try {
            //load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options)
        } catch (error: OutOfMemoryError) {
            error.printStackTrace()
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
        } catch (error: OutOfMemoryError) {
            error.printStackTrace()
        }

        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        val canvas = Canvas(scaledBitmap)
        canvas.matrix = scaleMatrix
        canvas.drawBitmap(bmp, middleX - bmp.height / 2, middleY - bmp.height / 2,
                Paint(Paint.FILTER_BITMAP_FLAG))
        //check the rotation of the image and display it properly
        try {
            val exif = ExifInterface(filePath)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
            val matrix = Matrix()
            when (orientation) {
                6 -> matrix.postRotate(90f)
                3 -> matrix.postRotate(180f)
                8 -> matrix.postRotate(270f)
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap!!.width,
                    scaledBitmap.height, matrix, true)
        } catch (error: IOException) {
            error.printStackTrace()
        }

        if (!outputFile.exists()) {
            val parent = File(outputFile.parent)
            if (!parent.exists()) {
                parent.mkdirs()
            }
            outputFile.createNewFile()
        }
        val uriString = outputFile.absolutePath
        try {
            val out = FileOutputStream(uriString)
            //write the compressed bitmap at the destination specified by filename.
            scaledBitmap?.compress(Bitmap.CompressFormat.JPEG, 80, out)
        } catch (error: FileNotFoundException) {
            error.printStackTrace()
        }
        return Uri.parse(uriString)
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight)
            val widthRatio = Math.round(width.toFloat() / reqWidth)
            inSampleSize = if (heightRatio < widthRatio) {
                heightRatio
            } else {
                widthRatio
            }
        }
        val totalPixels = width * height
        val totalReqPixelsCap = reqWidth * reqHeight * 2
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }
        return inSampleSize
    }
}