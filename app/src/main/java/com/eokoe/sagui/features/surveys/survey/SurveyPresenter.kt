package com.eokoe.sagui.features.surveys.survey

import com.eokoe.sagui.data.entities.*
import com.eokoe.sagui.data.model.SurveyModel
import com.eokoe.sagui.features.base.presenter.BasePresenterImpl
import io.reactivex.Observable
import io.reactivex.observers.DisposableObserver
import io.realm.RealmList

/**
 * @author Pedro Silva
 */
class SurveyPresenter constructor(private val surveyModel: SurveyModel)
    : SurveyContract.Presenter, BasePresenterImpl<SurveyContract.View>() {

    private var questions: List<Question>? = null
    private var total: Int = 0
    private var currentQuestion: Int = 0

    private var surveyId: String? = null
    private val answers = ArrayList<Answer>()

    override fun setSurvey(survey: Survey) {
        surveyId = survey.id
        questions = survey.questions
        total = questions!!.size
        view?.updateProgress(0, total)
    }

    override fun start() {
        if (currentQuestion >= total) {
            view?.finalize(answers)
            return
        }
        view?.showQuestion(questions!![currentQuestion])
    }

    private fun next() {
        if (currentQuestion < total) {
            view?.updateProgress(++currentQuestion, total)
        }
        if (currentQuestion >= total) {
            view?.finalize(answers)
            return
        }
        view?.loadQuestion(questions!![currentQuestion])
    }

    override fun answer(questionId: String, answer: Answer) {
        answers.add(Answer(questionId, answer.unitName, answer.value))
        next()
    }

    override fun answer(questionId: String, answer: String) {
        answers.add(Answer(questionId = questionId, value = answer))
        next()
    }

    override fun sendAnswers(answers: List<Answer>, location: LatLong?): Observable<Submissions> {
        view?.showLoading()
        val submissions = Submissions(
                surveyId = surveyId,
                location = location,
                answers = RealmList(*answers.toTypedArray())
        )
        return exec(surveyModel.sendAnswers(submissions), SendAnswersObserver())
    }

    inner class SendAnswersObserver : DisposableObserver<Submissions>() {
        override fun onNext(submissions: Submissions) {
            view?.answersSent()
        }

        override fun onComplete() {
            view?.hideLoading()
        }

        override fun onError(error: Throwable) {
            view?.showError(error)
        }
    }
}