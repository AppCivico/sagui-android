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

    private val itemList = ArrayList<E>()

    var items: Collection<E>?
        get() = itemList
        set(value) {
            itemList.clear()
            if (value != null && value.isNotEmpty())
                itemList.addAll(value)
            notifyDataSetChanged()
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

    open inner class SimpleViewHolder(view: View) : RecyclerView.ViewHolder(view)

    open inner class TextViewHolder : RecyclerView.ViewHolder {

        private var textView: TextView

        constructor(view: View, @IdRes idResText: Int) : super(view) {
            textView = view.findViewById(idResText) as TextView
        }

        constructor(view: View, @IdRes idResText: Int, @StringRes text: Int) : super(view) {
            textView = view.findViewById(idResText) as TextView
            bind(text)
        }

        constructor(view: View, @IdRes idResText: Int, text: String) : super(view) {
            textView = view.findViewById(idResText) as TextView
            bind(text)
        }

        fun bind(text: String) {
            textView.text = text
        }

        fun bind(@StringRes text: Int) {
            bind(itemView.context.getString(text))
        }
    }
}