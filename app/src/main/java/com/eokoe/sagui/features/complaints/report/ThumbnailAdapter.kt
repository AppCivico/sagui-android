package com.eokoe.sagui.features.complaints.report

import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Asset
import com.eokoe.sagui.extensions.getRealPath
import com.eokoe.sagui.features.base.view.RecyclerViewAdapter
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
            val bitmap = BitmapFactory.decodeFile(file.uri.getRealPath(itemView.context))
//            val thumbnail = ThumbnailUtils.extractThumbnail(bitmap, THUMBSIZE, THUMBSIZE)
            itemView.ivThumbnail.setImageBitmap(bitmap)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(file: Asset)
    }
}