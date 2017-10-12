package com.eokoe.sagui.widgets.listeners

import android.graphics.drawable.Animatable
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.ImageInfo

class FrescoWidthControllerListener(private val draweeView: SimpleDraweeView, imagePath: String?): BaseControllerListener<ImageInfo>() {
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

    private fun updateViewSize(imageInfo: ImageInfo?) {
        if (imageInfo != null) {
            val ratio = imageInfo.width.toFloat() / imageInfo.height.toFloat()
            val layoutParams = draweeView.layoutParams
            layoutParams.width = (layoutParams.height.toFloat() * ratio).toInt()
            draweeView.aspectRatio = ratio
            draweeView.layoutParams = layoutParams
        }
    }
}