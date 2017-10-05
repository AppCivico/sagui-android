package com.eokoe.sagui.features.complaints.report

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Asset
import com.eokoe.sagui.extensions.getRealPath
import com.eokoe.sagui.features.base.view.RecyclerViewAdapter
import com.eokoe.sagui.utils.UnitUtils
import kotlinx.android.synthetic.main.item_thumbnail.view.*

/**
 * @author Pedro Silva
 * @since 25/09/17
 */
class ThumbnailAdapter : RecyclerViewAdapter<Asset, RecyclerView.ViewHolder>() {
    var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(inflate(R.layout.item_thumbnail, parent))

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

        fun bind(file: Asset) {
            val fileUri = file.uri.getRealPath(itemView.context)
            val thumbnail: Bitmap?
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            val bitmap: Bitmap? = BitmapFactory.decodeFile(fileUri, options)
            thumbnail = if (options.outWidth != -1 && options.outHeight != -1) {
                val size = UnitUtils.dp2px(itemView.context, 100f).toInt()
                ThumbnailUtils.extractThumbnail(bitmap, size, size)
            } else {
                ThumbnailUtils.createVideoThumbnail(fileUri, MediaStore.Images.Thumbnails.MINI_KIND)
            }
            itemView.ivThumbnail.setImageBitmap(thumbnail)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(file: Asset)
    }
}