package com.eokoe.sagui.features.categories

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Category
import com.eokoe.sagui.features.base.view.RecyclerViewAdapter
import kotlinx.android.synthetic.main.item_category.view.*
import kotlinx.android.synthetic.main.item_error.view.*

/**
 * @author Pedro Silva
 * @since 16/08/17
 */
class CategoriesAdapter : RecyclerViewAdapter<Category, RecyclerView.ViewHolder> {

    var onItemClickListener: OnItemClickListener? = null

    constructor() : super()

    constructor(isShowLoading: Boolean) : super() {
        this.isShowLoading = isShowLoading
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            when (viewType) {
                ITEM_VIEW_TYPE -> ItemViewHolder(inflate(R.layout.item_category, parent))
                LOADING_VIEW_TYPE -> SimpleViewHolder(inflate(R.layout.item_progress, parent))
                ERROR_VIEW_TYPE -> ErrorViewHolder(inflate(R.layout.item_error, parent))
                else -> SimpleViewHolder(inflate(R.layout.item_category_header, parent))
            }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ITEM_VIEW_TYPE -> (holder as ItemViewHolder).bind(getItem(position))
            ERROR_VIEW_TYPE -> (holder as ErrorViewHolder).bind(error, retryClickListener)
        }
    }

    override fun getItemCount() = super.getItemCount() + 1

    override fun getItem(position: Int) = super.getItem(position - 1)

    override fun getItemViewType(position: Int) =
            when {
                hasError() -> ERROR_VIEW_TYPE
                isShowLoading -> LOADING_VIEW_TYPE
                position > 0 -> ITEM_VIEW_TYPE
                else -> HEADER_VIEW_TYPE
            }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(category: Category) {
            if (category.symbol != null) {
                val symbol = Character.toChars(Integer.parseInt(category.symbol, 16))
                itemView.tvSymbol.text = String(symbol)
            }
            itemView.tvCategoryName.text = category.name
            itemView.setOnClickListener {
                onItemClickListener?.onClick(category)
            }
        }
    }

    inner class ErrorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(error: String?, retryClickListener: OnRetryClickListener?) {
            itemView.tvError.text = error
            itemView.ivRefresh.setOnClickListener {
                retryClickListener?.retry()
            }
        }
    }

    inner class SpanSizeLookup : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return if (getItemViewType(position) == ITEM_VIEW_TYPE) 1
            else 3
        }
    }

    interface OnItemClickListener {
        fun onClick(category: Category)
    }
}