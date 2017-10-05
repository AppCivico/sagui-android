package com.eokoe.sagui.features.complaints.details

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Complaint
import com.eokoe.sagui.features.base.view.RecyclerViewAdapter

/**
 * @author Pedro Silva
 * @since 05/10/17
 */
class ComplaintDetailsAdapter(complaint: Complaint) : RecyclerViewAdapter<ComplaintDetailsAdapter.Item, RecyclerView.ViewHolder>() {
    var complaint: Complaint? = null
        set(value) {
            field = value
            setupItems()
        }

    init {
        this.complaint = complaint
    }

    init {

    }

    fun setupItems() {
        val items = ArrayList<Item>()
        items.add(Item(ItemType.HEADER))
        items.add(Item(ItemType.DETAILS))
        items.add(Item(ItemType.COMMENT_HEADER))
        items.add(Item(ItemType.COMMENT))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            when (ItemType.fromPosition(viewType)) {
                ItemType.HEADER -> SimpleViewHolder(inflate(R.layout.item_complaint_detail_header, parent))
                ItemType.DETAILS -> SimpleViewHolder(inflate(R.layout.item_complaint_detail, parent))
                ItemType.COMMENT_HEADER -> SimpleViewHolder(inflate(R.layout.item_complaint_detail_comment_header, parent))
                ItemType.COMMENT -> SimpleViewHolder(inflate(R.layout.item_complaint_detail_comment, parent))
            }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class Item(
            val type: ItemType
    )

    enum class ItemType {
        HEADER, DETAILS, COMMENT_HEADER, COMMENT;

        companion object {
            fun fromPosition(position: Int) = ItemType.values()[position]
        }
    }
}