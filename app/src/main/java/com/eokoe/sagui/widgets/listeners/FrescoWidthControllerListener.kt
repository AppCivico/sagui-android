package com.eokoe.sagui.widgets.listeners

import android.graphics.drawable.Animatable
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.ImageInfo

class FrescoWidthControllerListener(private val draweeView: SimpleDraweeView, imagePath: String): BaseControllerListener<ImageInfo>() {
    init {
        val controller = Fresco.newDraweeControllerBuilder()
                .setUri(imagePath)
                .setControllerListener(this)
                .build()
        draweeView.controller = controller
    }

    override fun onIntermediateImageSet(id: String?, imageInfo: ImageInfo?) {
        updateViewSize(imageInfo)
    }

    override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
        updateViewSize(imageInfo)
    }

    fun updateViewSize(imageInfo: ImageInfo?) {
        if (imageInfo != null) {
            val ratio = imageInfo.width / imageInfo.height.toFloat()
            draweeView.layoutParams.width = (draweeView.layoutParams.height * ratio).toInt()
            draweeView.aspectRatio = imageInfo.width.toFloat() / imageInfo.height
        }
    }
}