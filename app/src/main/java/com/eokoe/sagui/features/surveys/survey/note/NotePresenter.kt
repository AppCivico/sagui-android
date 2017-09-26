package com.eokoe.sagui.features.surveys.survey.note

import com.eokoe.sagui.data.entities.Comment
import com.eokoe.sagui.data.model.SaguiModel
import com.eokoe.sagui.features.base.presenter.BasePresenterImpl
import io.reactivex.Observable
import io.reactivex.observers.DisposableObserver

/**
 * @author Pedro Silva
 * @since 14/09/17
 */
class NotePresenter constructor(private val saguiModel: SaguiModel)
    : NoteContract.Presenter, BasePresenterImpl<NoteContract.View>() {

    override fun sendNote(comment: Comment): Observable<Comment> {
        view?.showLoading()
        return exec(saguiModel.saveComment(comment), SendNoteObserver())
    }

    inner class SendNoteObserver : DisposableObserver<Comment>() {
        override fun onNext(t: Comment) {
            view?.noteSent()
        }

        override fun onComplete() {
            view?.hideLoading()
        }

        override fun onError(error: Throwable) {
            view?.showError(error)
        }

    }
}