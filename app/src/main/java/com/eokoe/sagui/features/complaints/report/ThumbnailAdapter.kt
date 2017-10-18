package com.eokoe.sagui.features.complaints.report

import android.content.Context
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Asset
import com.eokoe.sagui.extensions.getRealPath
import com.eokoe.sagui.extensions.hide
import com.eokoe.sagui.extensions.show
import com.eokoe.sagui.features.base.view.RecyclerViewAdapter
import com.eokoe.sagui.utils.UnitUtils
import kotlinx.android.synthetic.main.item_report_thumbnail.view.*

/**
 * @author Pedro Silva
 * @since 25/09/17
 */
class ThumbnailAdapter : RecyclerViewAdapter<Asset, RecyclerView.ViewHolder>() {
    var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(inflate(R.layout.item_report_thumbnail, parent))

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
            with(itemView) {
                val fileUri = file.uri.getRealPath(context)
                val thumbnail = if (isImage(context, file)) {
                    val bitmap = BitmapFactory.decodeFile(fileUri)
                    val size = UnitUtils.dp2px(context, 100f).toInt()
                    ivPlay.hide()
                    ThumbnailUtils.extractThumbnail(bitmap, size, size)
                } else {
                    ivPlay.show()
                    ThumbnailUtils.createVideoThumbnail(fileUri, MediaStore.Images.Thumbnails.MINI_KIND)
                }
                if (thumbnail != null) {
                    ivThumbnail.setImageBitmap(thumbnail)
                } else {
                    ivPlay.hide()
                    val audioThumbnail = AppCompatResources.getDrawable(context, R.drawable.ic_audio)
                    ivThumbnail.setImageDrawable(audioThumbnail)
                }
            }
        }

        private fun isImage(context: Context, asset: Asset): Boolean {
            return if (asset.type != null) {
                asset.isImage
            } else {
                val fileUri = asset.uri.getRealPath(context)
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeFile(fileUri, options)
                options.outWidth != -1 && options.outHeight != -1
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(file: Asset)
    }
}