package com.eokoe.sagui.features.base.view

import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * @author Pedro Silva
 * @since 16/08/17
 */
abstract class RecyclerViewAdapter<E, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    protected val EMPTY_LIST_VIEW_TYPE = 0
    protected val HEADER_VIEW_TYPE = 1
    protected val ITEM_VIEW_TYPE = 2
    protected val LOADING_VIEW_TYPE = 3
    protected val ERROR_VIEW_TYPE = 4

    protected val itemList = ArrayList<E>()

    var items: Collection<E>?
        get() = itemList
        set(value) {
            itemList.clear()
            if (value != null && value.isNotEmpty())
                itemList.addAll(value)
            notifyDataSetChanged()
        }

    protected var error: String? = null
    protected var retryClickListener: OnRetryClickListener? = null


    var isShowLoading: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                notifyDataSetChanged()
            }
        }

    fun inflate(@LayoutRes layout: Int, parent: ViewGroup): View =
            LayoutInflater.from(parent.context).inflate(layout, parent, false)

    override fun getItemCount() = items!!.size

    open fun getItem(position: Int) = itemList[position]

    fun appendItems(items: Collection<E>?) {
        if (items!!.isNotEmpty()) {
            val count = itemCount
            itemList.addAll(items)
            if (count == 0) {
                notifyDataSetChanged()
            } else {
                notifyItemRangeChanged(count, itemList.size)
            }
        }
    }

    fun hasError() = error != null

    fun showError(error: String, retryClickListener: OnRetryClickListener? = null) {
        this.error = error
        this.retryClickListener = retryClickListener
        notifyDataSetChanged()
    }

    fun clearError() {
        this.error = null
        this.retryClickListener = null
        notifyDataSetChanged()
    }

    open inner class SimpleViewHolder(view: View) : RecyclerView.ViewHolder(view)

    open inner class TextViewHolder : RecyclerView.ViewHolder {

        private var textView: TextView

        constructor(view: View, @IdRes idResText: Int) : super(view) {
            textView = view.findViewById(idResText)
        }

        constructor(view: View, @IdRes idResText: Int, @StringRes text: Int) : super(view) {
            textView = view.findViewById(idResText)
            bind(text)
        }

        constructor(view: View, @IdRes idResText: Int, text: String) : super(view) {
            textView = view.findViewById(idResText)
            bind(text)
        }

        fun bind(text: String) {
            textView.text = text
        }

        fun bind(@StringRes text: Int) {
            bind(itemView.context.getString(text))
        }
    }

    interface OnRetryClickListener {
        fun retry()
    }
}