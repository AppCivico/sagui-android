package com.eokoe.sagui.features.enterprises

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Asset
import com.eokoe.sagui.features.base.view.RecyclerViewAdapter
import com.eokoe.sagui.widgets.listeners.FrescoWidthControllerListener
import kotlinx.android.synthetic.main.item_enterprise_image.view.*



/**
 * @author Pedro Silva
 * @since 25/09/17
 */
class ImageAdapter(items: List<Asset>) : RecyclerViewAdapter<Asset, RecyclerView.ViewHolder>() {
    init {
        this.items = items
    }
    var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(inflate(R.layout.item_enterprise_image, parent))

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

        fun bind(image: Asset) {
            FrescoWidthControllerListener(itemView.ivEnterprise, image.thumbnail ?: image.remotePath)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(image: Asset)
    }
}