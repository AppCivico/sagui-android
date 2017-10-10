package com.eokoe.sagui.features.notifications

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Notification
import com.eokoe.sagui.features.base.view.RecyclerViewAdapter
import kotlinx.android.synthetic.main.item_notification.view.*

/**
 * @author Pedro Silva
 * @since 06/10/17
 */
class NotificationsAdapter : RecyclerViewAdapter<Notification, RecyclerView.ViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            when (viewType) {
                ITEM_VIEW_TYPE -> ItemViewHolder(inflate(R.layout.item_notification, parent))
                EMPTY_LIST_VIEW_TYPE -> SimpleViewHolder(inflate(R.layout.item_notification_empty, parent))
                else -> TextViewHolder(inflate(R.layout.item_header, parent), R.id.title, R.string.unread)
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

        fun bind(notification: Notification) {
            itemView.tvEvent.text = when (notification.event) {
                Notification.Event.CAUSE -> "Reclamação se tornou causa"
                Notification.Event.COMMENT -> "Novo comentário"
                Notification.Event.CONFIRMATION -> "Nova contribuição"
            }
            itemView.tvMessage.text = notification.message
        }
    }

    interface OnItemClickListener {
        fun onItemClick(notification: Notification)
    }
}