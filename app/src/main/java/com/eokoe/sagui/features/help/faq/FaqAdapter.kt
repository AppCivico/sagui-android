package com.eokoe.sagui.features.help.faq

import android.support.annotation.StringRes
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.eokoe.sagui.R
import com.eokoe.sagui.extensions.collapse
import com.eokoe.sagui.extensions.expand
import com.eokoe.sagui.extensions.isVisible
import com.eokoe.sagui.features.base.view.RecyclerViewAdapter
import kotlinx.android.synthetic.main.item_faq.view.*

/**
 * @author Pedro Silva
 * @since 12/10/17
 */
class FaqAdapter : RecyclerViewAdapter<FaqAdapter.Item, FaqAdapter.ItemViewHolder>() {

    init {
        val items = ArrayList<Item>()
        items.add(Item("Empreendimentos", R.string.faq_enterprise))
        items.add(Item("Enquetes", R.string.faq_survey))
        items.add(Item("Apontamentos", R.string.faq_complaint))
        this.items = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ItemViewHolder(inflate(R.layout.item_faq, parent))

    override fun onBindViewHolder(holder: FaqAdapter.ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            itemView.setOnClickListener {
                if (adapterPosition >= 0) {
                    if (itemView.tvFaqAnswer.isVisible) {
                        itemView.tvFaqAnswer.collapse()
                    } else {
                        itemView.tvFaqAnswer.expand()
                    }
                }
            }
        }

        fun bind(item: Item) {
            itemView.tvTitle.text = item.title
            itemView.tvFaqAnswer.setText(item.answer)
        }
    }

    class Item(val title: String, @StringRes val answer: Int)
}