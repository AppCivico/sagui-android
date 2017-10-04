package com.eokoe.sagui.features.complaints.report

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Asset
import com.eokoe.sagui.data.entities.Complaint
import com.eokoe.sagui.features.base.view.RecyclerViewAdapter
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.item_report_action.view.*
import kotlinx.android.synthetic.main.item_report_textarea.view.*
import kotlinx.android.synthetic.main.item_report_thumbnails.view.*
import kotlinx.android.synthetic.main.item_report_title.view.*

/**
 * @author Pedro Silva
 * @since 25/09/17
 */
class ReportAdapter : RecyclerViewAdapter<ReportAdapter.Item, RecyclerView.ViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null

    val titleChangeSubject: PublishSubject<String> = PublishSubject.create()
    val descriptionChangeSubject: PublishSubject<String> = PublishSubject.create()

    init {
        val items = ArrayList<Item>()
        items.add(Item(ItemType.DESCRIPTION))
        items.add(Item(ItemType.DIVIDER))
        items.add(Item(ItemType.THUMBNAILS, value = ArrayList<Asset>()))
        items.add(Item(ItemType.DIVIDER))
        items.add(Item(ItemType.TITLE))
        items.add(Item(ItemType.DIVIDER))
        items.add(Item(ItemType.LOCATION, R.drawable.ic_location, R.string.occurrence_place))
        items.add(Item(ItemType.DIVIDER))
//        items.add(Item(ItemType.INSERT_PHOTO_VIDEO, R.drawable.ic_photo, R.string.insert_photo_video))
//        items.add(Item(ItemType.DIVIDER))
        items.add(Item(ItemType.CAMERA, R.drawable.ic_photo_camera, R.string.camera))
        items.add(Item(ItemType.DIVIDER))
//        items.add(Item(ItemType.INSERT_AUDIO, R.drawable.ic_audio, R.string.insert_audio))
//        items.add(Item(ItemType.DIVIDER))
        this.items = items
    }

    fun setComplaint(complaint: Complaint) {
        items?.forEachIndexed { index, item ->
            if (item.type == ItemType.LOCATION && item.value != complaint.address) {
                item.value = complaint.address
                notifyItemChanged(index)
            } else if (item.type == ItemType.THUMBNAILS) {
                item.value = complaint.files
                notifyItemChanged(index)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            when (ItemType.fromPosition(viewType)) {
                ItemType.TITLE -> TitleViewHolder(inflate(R.layout.item_report_title, parent))
                ItemType.DESCRIPTION -> DescriptionViewHolder(inflate(R.layout.item_report_textarea, parent))
                ItemType.DIVIDER -> SimpleViewHolder(inflate(R.layout.divider_dark, parent))
                ItemType.THUMBNAILS -> ThumbnailsViewHolder(inflate(R.layout.item_report_thumbnails, parent))
                else -> ActionViewHolder(inflate(R.layout.item_report_action, parent))
            }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ActionViewHolder) {
            holder.bind(getItem(position))
        } else if (holder is ThumbnailsViewHolder) {
            val assets = getItem(position).value as List<Asset>
            holder.bind(assets)
        }
    }

    override fun getItemViewType(position: Int) = getItem(position).type.ordinal

    inner class TitleViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        init {
            RxTextView.textChangeEvents(itemView.etTitle)
                    .map {return@map it.text().toString()}
                    .subscribe(titleChangeSubject)
        }
    }

    inner class DescriptionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        init {
            RxTextView.textChangeEvents(itemView.tvDescription)
                    .map {return@map it.text().toString()}
                    .subscribe(descriptionChangeSubject)
        }
    }

    inner class ThumbnailsViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        init {
            itemView.rvThumbnails.setHasFixedSize(true)
            itemView.rvThumbnails.adapter = ThumbnailAdapter()
            itemView.rvThumbnails.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        }

        fun bind(assets: List<Asset>) {
            (itemView.rvThumbnails.adapter as ThumbnailAdapter).items = assets
            /*if (assets.isNotEmpty()) {
                itemView.show()
            } else {
                itemView.hide()
            }*/
        }
    }

    inner class ActionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        init {
            itemView.setOnClickListener {
                if (adapterPosition > -1) {
                    onItemClickListener?.onItemClick(getItem(adapterPosition).type)
                }
            }
        }

        fun bind(item: Item) {
            itemView.ivActionIcon.setImageResource(item.icon!!)
            if (item.value == null) {
                itemView.tvActionName.setText(item.actionName!!)
            } else {
                itemView.tvActionName.text = item.value as? String
            }
        }
    }

    class Item(
            val type: ItemType,
            @DrawableRes
            val icon: Int? = null,
            @StringRes
            val actionName: Int? = null,
            var value: Any? = null
    )

    enum class ItemType {
        DIVIDER, DESCRIPTION, TITLE, LOCATION, CAMERA, INSERT_PHOTO_VIDEO, INSERT_AUDIO, THUMBNAILS;

        companion object {
            fun fromPosition(position: Int) = ItemType.values()[position]
        }
    }

    interface OnItemClickListener {
        fun onItemClick(itemType: ItemType)
    }
}