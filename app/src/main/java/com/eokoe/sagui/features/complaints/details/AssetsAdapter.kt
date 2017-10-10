package com.eokoe.sagui.features.complaints.details

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Asset
import com.eokoe.sagui.extensions.hide
import com.eokoe.sagui.extensions.show
import com.eokoe.sagui.features.base.view.RecyclerViewAdapter
import com.eokoe.sagui.widgets.listeners.FrescoWidthControllerListener
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
                asset.isImage ->
                    FrescoWidthControllerListener(itemView.ivThumbnail, asset.uri.toString())
                else -> {
                    itemView.ivPlay.show()
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(asset: Asset)
    }
}