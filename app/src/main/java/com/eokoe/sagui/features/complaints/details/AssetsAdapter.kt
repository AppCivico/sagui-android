package com.eokoe.sagui.features.complaints.details

import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Asset
import com.eokoe.sagui.extensions.hide
import com.eokoe.sagui.extensions.show
import com.eokoe.sagui.features.base.view.RecyclerViewAdapter
import com.eokoe.sagui.widgets.listeners.FrescoWidthControllerListener
import com.facebook.drawee.drawable.ScalingUtils
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
                    onItemClickListener?.onItemClick(adapterPosition, getItem(adapterPosition))
                }
            }
        }

        fun bind(asset: Asset) {
            with(itemView) {
                ivPlay.hide()
                ivThumbnail.hierarchy.setPlaceholderImage(null)
                ivThumbnail.setImageURI("")

                when {
                    asset.isImage -> {
                        FrescoWidthControllerListener(ivThumbnail,
                                asset.thumbnail ?: asset.uri.toString())
                    }
                    asset.isVideo -> {
                        FrescoWidthControllerListener(ivThumbnail, asset.thumbnail)
                        ivPlay.show()
                    }
                    else -> {
                        val drawable = AppCompatResources.getDrawable(context, R.drawable.ic_audio)
                        ivThumbnail.hierarchy.setPlaceholderImage(drawable,
                                ScalingUtils.ScaleType.CENTER_INSIDE)
                    }
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, asset: Asset)
    }
}