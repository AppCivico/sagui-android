package com.eokoe.sagui.features.complaints.details

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Comment
import com.eokoe.sagui.data.entities.Complaint
import com.eokoe.sagui.extensions.format
import com.eokoe.sagui.features.base.view.RecyclerViewAdapter
import kotlinx.android.synthetic.main.item_complaint_detail.view.*
import kotlinx.android.synthetic.main.item_complaint_detail_comment.view.*

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

    private fun setupItems() {
        val items = ArrayList<Item>()
        items.add(Item(ItemType.HEADER))
        items.add(Item(ItemType.DETAILS, complaint))
        if (complaint?.comments?.size ?: 0 > 0) {
            items.add(Item(ItemType.COMMENT_HEADER))
            complaint?.comments?.forEach {
                items.add(Item(ItemType.COMMENT, it))
            }
        }
        this.items = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            when (ItemType.fromPosition(viewType)) {
                ItemType.HEADER -> SimpleViewHolder(inflate(R.layout.item_complaint_detail_header, parent))
                ItemType.DETAILS -> DetailsViewHolder(inflate(R.layout.item_complaint_detail, parent))
                ItemType.COMMENT_HEADER -> SimpleViewHolder(inflate(R.layout.item_complaint_detail_comment_header, parent))
                ItemType.COMMENT -> CommentViewHolder(inflate(R.layout.item_complaint_detail_comment, parent))
            }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DetailsViewHolder -> holder.bind(complaint!!)
            is CommentViewHolder -> holder.bind(getItem(position).value as Comment)
        }
    }

    override fun getItemViewType(position: Int) = getItem(position).type.ordinal

    class DetailsViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(complaint: Complaint) {
            val context = itemView.context
            val resources = context.resources
            itemView.tvTitle.text = complaint.title
            itemView.tvCategoryName.text = complaint.category?.name
            itemView.tvLocation.text = complaint.address
            itemView.tvQtyConfirmations.text = resources.getQuantityString(R.plurals.qty_confirmations,
                    complaint.confirmations, complaint.confirmations)
            val remain = complaint.numToBecameCause - complaint.confirmations
            if (remain > 0) {
                itemView.tvQtyRemain.text = resources.getQuantityString(R.plurals.qty_remain, remain, remain)
            } else {
                itemView.tvQtyRemain.setText(R.string.occurrence_already)
            }
            itemView.tvDescription.text = complaint.description
        }
    }

    class CommentViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(comment: Comment) {
            itemView.tvCommentDate.text = comment.createdAt?.format("dd.MM.yyyy")
            itemView.tvComment.text = comment.content
        }
    }

    class Item(
            val type: ItemType,
            val value: Any? = null
    )

    enum class ItemType {
        HEADER, DETAILS, COMMENT_HEADER, COMMENT;

        companion object {
            fun fromPosition(position: Int) = ItemType.values()[position]
        }
    }
}