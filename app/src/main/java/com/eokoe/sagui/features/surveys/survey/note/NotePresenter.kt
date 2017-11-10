package com.eokoe.sagui.features.surveys.survey.note

import com.eokoe.sagui.data.entities.Comment
import com.eokoe.sagui.data.model.SaguiModel
import com.eokoe.sagui.features.base.DefaultObserver
import com.eokoe.sagui.features.base.presenter.BasePresenterImpl

/**
 * @author Pedro Silva
 * @since 14/09/17
 */
class NotePresenter constructor(private val saguiModel: SaguiModel)
    : NoteContract.Presenter, BasePresenterImpl<NoteContract.View>() {

    override fun sendNote(comment: Comment) =
            exec(saguiModel.saveComment(comment), SendNoteObserver())

    inner class SendNoteObserver : DefaultObserver<Comment>(view) {
        override fun onSuccess(result: Comment?) {
            view?.noteSent()
        }
    }
}