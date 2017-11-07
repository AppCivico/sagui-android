package com.eokoe.sagui.features.pendencies

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Pendency
import com.eokoe.sagui.features.base.view.RecyclerViewAdapter

/**
 * @author Pedro Silva
 * @since 06/10/17
 */
class PendenciesAdapter : RecyclerViewAdapter<Pendency, RecyclerView.ViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            when (viewType) {
                ITEM_VIEW_TYPE -> ItemViewHolder(inflate(R.layout.item_pendency, parent))
                EMPTY_LIST_VIEW_TYPE -> SimpleViewHolder(inflate(R.layout.item_pendency_empty, parent))
                else -> TextViewHolder(inflate(R.layout.item_header, parent), R.id.title, R.string.your_pendencies)
            }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is ItemViewHolder) {
            holder.bind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int) = when {
        hasError() -> ERROR_VIEW_TYPE
        isShowLoading -> LOADING_VIEW_TYPE
        itemCount == 1 -> EMPTY_LIST_VIEW_TYPE
        position > 0 -> ITEM_VIEW_TYPE
        else -> HEADER_VIEW_TYPE
    }

    override fun getItemCount() = super.getItemCount() + 1

    override fun getItem(position: Int) = super.getItem(position - 1)

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            itemView.setOnClickListener {
                if (adapterPosition > -1) {
                    onItemClickListener?.onItemClick(getItem(adapterPosition))
                }
            }
        }

        fun bind(pendency: Pendency) {
            TODO("not implemented")
        }
    }

    interface OnItemClickListener {
        fun onItemClick(pendency: Pendency)
    }
}