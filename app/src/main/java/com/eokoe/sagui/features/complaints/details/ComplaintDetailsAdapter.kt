package com.eokoe.sagui.features.complaints.details

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Asset
import com.eokoe.sagui.data.entities.Comment
import com.eokoe.sagui.data.entities.Complaint
import com.eokoe.sagui.extensions.format
import com.eokoe.sagui.features.base.view.RecyclerViewAdapter
import kotlinx.android.synthetic.main.item_complaint_detail.view.*
import kotlinx.android.synthetic.main.item_complaint_detail_assets.view.*
import kotlinx.android.synthetic.main.item_complaint_detail_comment.view.*

/**
 * @author Pedro Silva
 * @since 05/10/17
 */
class ComplaintDetailsAdapter : RecyclerViewAdapter<ComplaintDetailsAdapter.Item, RecyclerView.ViewHolder>() {
    var complaint: Complaint? = null
        set(value) {
            field = value
            setupItems()
        }
    var onImageClickListener: AssetsAdapter.OnItemClickListener? = null

    private fun setupItems() {
        val items = ArrayList<Item>()
        if (complaint != null) {
            items.add(Item(ItemType.HEADER, R.string.explain_complaint))
            items.add(Item(ItemType.DETAILS))
            items.add(Item(ItemType.ASSETS))
            if (complaint?.comments?.isNotEmpty() == true) {
                items.add(Item(ItemType.COMMENT_HEADER))
                complaint?.comments?.forEach {
                    items.add(Item(ItemType.COMMENT, it))
                }
            }
        } else {
            items.add(Item(ItemType.LOADING))
        }
        this.items = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            when (ItemType.fromPosition(viewType)) {
                ItemType.HEADER -> HeaderViewHolder(inflate(R.layout.item_complaint_detail_header, parent))
                ItemType.DETAILS -> DetailsViewHolder(inflate(R.layout.item_complaint_detail, parent))
                ItemType.COMMENT_HEADER -> SimpleViewHolder(inflate(R.layout.item_complaint_detail_comment_header, parent))
                ItemType.COMMENT -> CommentViewHolder(inflate(R.layout.item_complaint_detail_comment, parent))
                ItemType.ASSETS -> AssetsViewHolder(inflate(R.layout.item_complaint_detail_assets, parent))
                ItemType.LOADING -> SimpleViewHolder(inflate(R.layout.item_progress, parent))
            }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DetailsViewHolder -> holder.bind(complaint!!)
            is CommentViewHolder -> holder.bind(getItem(position).value as Comment)
            is AssetsViewHolder -> holder.bind(complaint!!.files)
            is HeaderViewHolder -> holder.bind(complaint)
        }
    }

    override fun getItemViewType(position: Int) = getItem(position).type.ordinal

    inner class DetailsViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(complaint: Complaint) {
            with(itemView) {
                val resources = context.resources
                tvTitle.text = complaint.title
                tvCategoryName.text = complaint.category?.name
                tvLocation.text = complaint.address
                tvQtyConfirmations.text = resources.getQuantityString(R.plurals.qty_confirmations,
                        complaint.confirmations, complaint.confirmations)
                val remain = complaint.numToBecameCause - complaint.confirmations
                if (remain > 0) {
                    tvQtyRemain.text = resources.getQuantityString(R.plurals.qty_remain, remain, remain)
                } else {
                    tvQtyRemain.setText(R.string.occurrence_already)
                }
                etDescription.text = complaint.description
            }
        }
    }

    inner class CommentViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(comment: Comment) {
            with(itemView) {
                tvCommentDate.text = comment.createdAt?.format("dd.MM.yyyy")
                tvComment.text = comment.content
            }
        }
    }

    inner class AssetsViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        init {
            with(itemView) {
                rvAssets.setHasFixedSize(true)
                val assetsAdapter = AssetsAdapter()
                assetsAdapter.onItemClickListener = onImageClickListener
                rvAssets.adapter = assetsAdapter
                rvAssets.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }
        }

        fun bind(assets: List<Asset>) {
            (itemView.rvAssets.adapter as AssetsAdapter).items = assets
        }
    }

    inner class HeaderViewHolder(view: View) : TextViewHolder(view, R.id.tvExplainComplaint) {
        fun bind(complaint: Complaint?) {
            val explainComplaint = itemView.context.getString(R.string.explain_complaint, complaint?.numToBecameCause)
            super.bind(explainComplaint)
        }
    }

    class Item(val type: ItemType, val value: Any? = null)

    enum class ItemType {
        HEADER, DETAILS, COMMENT_HEADER, COMMENT, ASSETS, LOADING;

        companion object {
            fun fromPosition(position: Int) = ItemType.values()[position]
        }
    }
}