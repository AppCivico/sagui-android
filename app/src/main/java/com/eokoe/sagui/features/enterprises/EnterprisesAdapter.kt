package com.eokoe.sagui.features.enterprises

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Enterprise
import com.eokoe.sagui.features.base.view.RecyclerViewAdapter
import kotlinx.android.synthetic.main.item_enterprise.view.*
import kotlinx.android.synthetic.main.item_error.view.*

/**
 * @author Pedro Silva
 * @since 29/08/17
 */
class EnterprisesAdapter : RecyclerViewAdapter<Enterprise, RecyclerView.ViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null
    var onImageClickListener: ImageAdapter.OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            when (viewType) {
                ITEM_VIEW_TYPE -> ItemViewHolder(inflate(R.layout.item_enterprise, parent))
                LOADING_VIEW_TYPE -> SimpleViewHolder(inflate(R.layout.item_progress, parent))
                ERROR_VIEW_TYPE -> ErrorViewHolder(inflate(R.layout.item_error, parent))
                EMPTY_LIST_VIEW_TYPE -> SimpleViewHolder(inflate(R.layout.item_enterprises_empty, parent))
                else -> TextViewHolder(inflate(R.layout.item_header, parent), R.id.title, R.string.choose_enterprise)
            }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ITEM_VIEW_TYPE -> (holder as ItemViewHolder).bind(getItem(position))
            ERROR_VIEW_TYPE -> (holder as ErrorViewHolder).bind(error, retryClickListener)
        }
    }

    override fun getItemViewType(position: Int) =
            when {
                hasError() -> ERROR_VIEW_TYPE
                isShowLoading -> LOADING_VIEW_TYPE
                itemCount == 1 -> EMPTY_LIST_VIEW_TYPE
                position > 0 -> ITEM_VIEW_TYPE
                else -> HEADER_VIEW_TYPE
            }

    override fun getItemCount() = super.getItemCount() + 1

    override fun getItem(position: Int) = super.getItem(position - 1)

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(enterprise: Enterprise) {
            with(itemView) {
                val cover = enterprise.images.firstOrNull()
                imgEnterprise.setImageURI(cover?.thumbnail ?: cover?.remotePath)
                tvEnterpriseName.text = enterprise.name
                tvLocation.text = enterprise.address
                tvQtyConfirmations.text = itemView.resources.getQuantityString(
                        R.plurals.qty_complaints, enterprise.data.complaints, enterprise.data.complaints)
                tvQtyCases.text = itemView.resources.getQuantityString(
                        R.plurals.qty_causes, enterprise.data.cases, enterprise.data.cases)
                setOnClickListener {
                    onItemClickListener?.onClick(enterprise)
                }
                if (enterprise.images.isNotEmpty()) {
                    rvImgEnterprise.setHasFixedSize(true)
                    val imageAdapter = ImageAdapter(enterprise.images)
                    imageAdapter.onItemClickListener = onImageClickListener
                    rvImgEnterprise.adapter = imageAdapter
                    rvImgEnterprise.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                }
            }
        }
    }

    inner class ErrorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(error: String?, retryClickListener: OnRetryClickListener?) {
            with(itemView) {
                tvError.text = error
                setOnClickListener {
                    retryClickListener?.retry()
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onClick(enterprise: Enterprise)
    }
}