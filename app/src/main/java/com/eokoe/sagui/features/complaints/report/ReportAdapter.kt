package com.eokoe.sagui.features.complaints.report

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.eokoe.sagui.R
import com.eokoe.sagui.extensions.invisible
import com.eokoe.sagui.extensions.show
import com.eokoe.sagui.features.base.view.RecyclerViewAdapter
import kotlinx.android.synthetic.main.item_report_action.view.*

/**
 * @author Pedro Silva
 * @since 25/09/17
 */
class ReportAdapter : RecyclerViewAdapter<ReportAdapter.Item, RecyclerView.ViewHolder>() {

    init {
        val items = ArrayList<Item>()
        items.add(Item(ItemType.DESCRIPTION))
        items.add(Item(ItemType.DIVIDER))
        items.add(Item(ItemType.TITLE, R.drawable.ic_title, R.string.title))
        items.add(Item(ItemType.DIVIDER))
        items.add(Item(ItemType.LOCATION, R.drawable.ic_location, R.string.occurrence_place, true))
        items.add(Item(ItemType.DIVIDER))
        items.add(Item(ItemType.INSERT_IMAGE, R.drawable.ic_photo_camera, R.string.insert_photo, true))
        items.add(Item(ItemType.DIVIDER))
        items.add(Item(ItemType.INSERT_VIDEO, R.drawable.ic_video, R.string.insert_video, true))
        items.add(Item(ItemType.DIVIDER))
        items.add(Item(ItemType.INSERT_AUDIO, R.drawable.ic_audio, R.string.insert_audio, true))
        items.add(Item(ItemType.DIVIDER))
        this.items = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            when (ItemType.fromPosition(viewType)) {
                ItemType.DIVIDER -> SimpleViewHolder(inflate(R.layout.divider_dark, parent))
                ItemType.DESCRIPTION -> SimpleViewHolder(inflate(R.layout.item_report_textarea, parent))
                else -> ActionViewHolder(inflate(R.layout.item_report_action, parent))
            }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ActionViewHolder) {
            holder.bind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int) = getItem(position).type.ordinal

    inner class ActionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: Item) {
            itemView.ivActionIcon.setImageResource(item.icon!!)
            itemView.tvActionName.setText(item.actionName!!)
            if (item.showArrow) {
                itemView.ivArrow.show()
            } else {
                itemView.ivArrow.invisible()
            }
        }
    }

    class Item(
            val type: ItemType,
            @DrawableRes
            val icon: Int? = null,
            @StringRes
            val actionName: Int? = null,
            val showArrow: Boolean = false
    )

    enum class ItemType {
        DIVIDER, DESCRIPTION, TITLE, LOCATION, INSERT_IMAGE, INSERT_VIDEO, INSERT_AUDIO;

        companion object {
            fun fromPosition(position: Int) = ItemType.values()[position]
        }
    }
}