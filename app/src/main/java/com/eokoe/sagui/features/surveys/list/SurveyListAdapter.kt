package com.eokoe.sagui.features.surveys.list

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.eokoe.sagui.R
import com.eokoe.sagui.data.entities.Survey
import com.eokoe.sagui.features.base.view.RecyclerViewAdapter
import kotlinx.android.synthetic.main.item_error.view.*
import kotlinx.android.synthetic.main.item_survey.view.*

/**
 * @author Pedro Silva
 * @since 16/08/17
 */
class SurveyListAdapter : RecyclerViewAdapter<Survey, RecyclerView.ViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null

    fun markHasAnswered(id: String) {
        items = items?.map {
            it.hasAnswer = it.id == id || it.hasAnswer
            it
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            when (viewType) {
                ITEM_VIEW_TYPE -> ItemViewHolder(inflate(R.layout.item_survey, parent))
                LOADING_VIEW_TYPE -> SimpleViewHolder(inflate(R.layout.item_progress, parent))
                ERROR_VIEW_TYPE -> ErrorViewHolder(inflate(R.layout.item_error, parent))
                EMPTY_LIST_VIEW_TYPE -> SimpleViewHolder(inflate(R.layout.item_survey_empty, parent))
                else -> TextViewHolder(inflate(R.layout.item_header, parent), R.id.title, R.string.choose_survey)
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
                itemCount == 1 -> EMPTY_LIST_VIEW_TYPE
                position > 0 -> ITEM_VIEW_TYPE
                else -> HEADER_VIEW_TYPE
            }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(survey: Survey) {
            itemView.run {
                /*val bgColor = ContextCompat.getColor(context,
                        if (!survey.hasAnswer) R.color.bg_survey
                        else R.color.bg_survey_answered
                )
                setBackgroundColor(bgColor)*/
                tvSurveyTitle.text = survey.name
                setOnClickListener {
                    onItemClickListener?.onClick(survey)
                }
            }
        }
    }

    inner class ErrorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(error: String?, retryClickListener: OnRetryClickListener?) {
            itemView.run {
                tvError.text = error
                setOnClickListener {
                    retryClickListener?.retry()
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onClick(survey: Survey)
    }
}