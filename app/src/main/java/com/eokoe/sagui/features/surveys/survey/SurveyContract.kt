package com.eokoe.sagui.features.surveys.survey

import com.eokoe.sagui.data.entities.*
import com.eokoe.sagui.features.base.presenter.BasePresenter
import com.eokoe.sagui.features.base.view.ViewError
import com.eokoe.sagui.features.base.view.ViewLoading
import io.reactivex.Observable

/**
 * @author Pedro Silva
 */
interface SurveyContract {
    interface View : ViewLoading, ViewError {
        var currentProgress: Int
        fun showQuestion(question: Question, answer: Answer?)
        fun hideQuestions()
        fun updateProgress(index: Int, size: Int)
        fun finalize(answers: List<Answer>)
        fun answersSent(submissions: Submissions)
    }

    interface Presenter : BasePresenter<View> {
        fun setSurvey(survey: Survey)
        fun start()
        fun back()
        fun answer(questionId: String, answer: Answer)
        fun answer(questionId: String, answer: String)
        fun sendAnswers(answers: List<Answer>, location: LatLong? = null): Observable<Submissions>
    }
}