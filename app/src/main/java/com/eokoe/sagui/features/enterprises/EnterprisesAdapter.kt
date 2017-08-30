package com.eokoe.sagui.features.enterprises

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.features.base.view.RecyclerViewAdapter
import kotlinx.android.synthetic.main.item_enterprise.view.*

/**
 * @author Pedro Silva
 * @since 29/08/17
 */
class EnterprisesAdapter : RecyclerViewAdapter<Enterprise, RecyclerView.ViewHolder>() {

    private val HEADER_VIEW_TYPE = 0
    private val ITEM_VIEW_TYPE = 1

    var onItemClickListener: OnItemClickListener? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == ITEM_VIEW_TYPE) {
            (holder as ItemViewHolder).bind(getItem(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_VIEW_TYPE) ItemViewHolder(inflate(R.layout.item_enterprise, parent))
        else TextViewHolder(inflate(R.layout.item_header, parent), R.id.title, R.string.choose_enterprise)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position > 0) ITEM_VIEW_TYPE
        else HEADER_VIEW_TYPE
    }

    override fun getItemCount() = super.getItemCount() + 1

    override fun getItem(position: Int) = super.getItem(position - 1)

    inner class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(enterprise: Enterprise) {
            itemView.tvEnterpriseName.text = enterprise.name
            itemView.tvLocation.text = enterprise.location.location
            itemView.tvQtyComplaints.text = itemView.resources.getQuantityString(
                    R.plurals.qty_complaints, enterprise.data.complaints, enterprise.data.complaints)
            itemView.tvQtyCases.text = itemView.resources.getQuantityString(
                    R.plurals.qty_causes, enterprise.data.cases, enterprise.data.cases)
            itemView.setOnClickListener {
                onItemClickListener?.onClick(enterprise)
            }
        }
    }

    interface OnItemClickListener {
        fun onClick(enterprise: Enterprise)
    }
}