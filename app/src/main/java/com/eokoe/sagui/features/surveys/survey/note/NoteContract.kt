package com.eokoe.sagui.features.surveys.survey.note

import com.eokoe.sagui.data.entities.Comment
import com.eokoe.sagui.features.base.presenter.BasePresenter
import com.eokoe.sagui.features.base.view.ViewError
import com.eokoe.sagui.features.base.view.ViewLoading
import io.reactivex.Observable

/**
 * @author Pedro Silva
 * @since 14/09/17
 */
interface NoteContract {
    interface View : ViewLoading, ViewError {
        fun noteSent()
    }

    interface Presenter : BasePresenter<View> {
        fun sendNote(comment: Comment): Observable<Comment>
    }
}