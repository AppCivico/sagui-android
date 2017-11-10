package com.eokoe.sagui.features.surveys.survey

import com.eokoe.sagui.data.entities.*
import com.eokoe.sagui.data.model.SaguiModel
import com.eokoe.sagui.features.base.DefaultObserver
import com.eokoe.sagui.features.base.presenter.BasePresenterImpl
import io.reactivex.Observable
import io.realm.RealmList

/**
 * @author Pedro Silva
 */
class SurveyPresenter(private val saguiModel: SaguiModel)
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
        currentQuestion = view?.currentProgress ?: 0
        exec(saguiModel.hasAnswer(survey), HasAnswerObserver())
    }

    override fun start() {
        if (currentQuestion >= total) {
            view?.finalize(answers)
            return
        }
        view?.showQuestion(questions!![currentQuestion], getAnswer(currentQuestion))
    }

    private fun next() {
        if (currentQuestion < total) {
            view?.updateProgress(++currentQuestion, total)
        }
        if (currentQuestion >= total) {
            view?.finalize(answers)
            return
        }
        view?.showQuestion(questions!![currentQuestion], getAnswer(currentQuestion))
    }

    override fun back() {
        if (currentQuestion > 0) {
            view?.updateProgress(--currentQuestion, total)
            view?.showQuestion(questions!![currentQuestion], getAnswer(currentQuestion))
        } else {
            view?.hideQuestions()
            answers.clear()
        }
    }

    override fun answer(questionId: String, answer: Answer) {
        if (answers.size <= currentQuestion) {
            answers.add(Answer(questionId, answer.unitName, answer.value))
        } else {
            answers[currentQuestion] = Answer(questionId, answer.unitName, answer.value)
        }
        next()
    }

    override fun answer(questionId: String, answer: String) {
        answer(questionId, Answer(value = answer))
    }

    private fun getAnswer(index: Int) = if (answers.size > index) answers[index] else null

    override fun sendAnswers(answers: List<Answer>, location: LatLong?): Observable<Submissions> {
        val submissions = Submissions(
                surveyId = surveyId,
                location = location,
                answers = RealmList(*answers.toTypedArray())
        )
        return exec(saguiModel.sendAnswers(submissions), SendAnswersObserver())
    }

    inner class HasAnswerObserver : DefaultObserver<Boolean>(view) {
        override fun onSuccess(result: Boolean?) {
            if (!result!!) {
                view?.onAnswersNotExists()
                view?.updateProgress(currentQuestion, total)
            } else {
                view?.onAnswersExists()
                view?.updateProgress(total, total)
            }
        }

        // FIXME: remove this method to limit to 1 answer per user
        override fun onComplete() {
            onSuccess(false)
            onHideLoading()
        }
    }

    inner class SendAnswersObserver : DefaultObserver<Submissions>(view) {
        override fun onSuccess(result: Submissions?) {
            view?.answersSent(result!!)
        }
    }
}