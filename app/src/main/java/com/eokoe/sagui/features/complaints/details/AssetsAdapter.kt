package com.eokoe.sagui.features.complaints.details

import android.graphics.drawable.Animatable
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Asset
import com.eokoe.sagui.extensions.hide
import com.eokoe.sagui.extensions.show
import com.eokoe.sagui.features.base.view.RecyclerViewAdapter
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.ImageInfo
import kotlinx.android.synthetic.main.item_complaint_detail_asset.view.*

/**
 * @author Pedro Silva
 * @since 25/09/17
 */
class AssetsAdapter : RecyclerViewAdapter<Asset, RecyclerView.ViewHolder>() {
    var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(inflate(R.layout.item_complaint_detail_asset, parent))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.bind(getItem(position))
        }
    }

    inner class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        init {
            itemView.setOnClickListener {
                if (adapterPosition > -1) {
                    onItemClickListener?.onItemClick(getItem(adapterPosition))
                }
            }
        }

        fun bind(asset: Asset) {
            itemView.ivPlay.hide()
            when {
                asset.type.matches("image/.+".toRegex()) ||
                        asset.remotePath!!.matches(".+\\.jpg.+".toRegex()) ->
                    ControllerListener(itemView.ivThumbnail, asset.uri)
                else -> {
                    itemView.ivPlay.show()
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(asset: Asset)
    }

    class ControllerListener(private val draweeView: SimpleDraweeView, imagePath: Uri) : BaseControllerListener<ImageInfo>() {
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
                draweeView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
//                draweeView.layoutParams.height = imageInfo.height
                draweeView.aspectRatio = imageInfo.width.toFloat() / imageInfo.height
            }
        }
    }
}