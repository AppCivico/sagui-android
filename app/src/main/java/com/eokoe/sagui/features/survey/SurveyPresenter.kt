package com.eokoe.sagui.features.survey

import com.eokoe.sagui.data.entities.Answer
import com.eokoe.sagui.data.entities.Question
import com.eokoe.sagui.data.entities.Survey
import com.eokoe.sagui.features.base.presenter.BasePresenterImpl

/**
 * @author Pedro Silva
 */
class SurveyPresenter : SurveyContract.Presenter, BasePresenterImpl<SurveyContract.View>() {

    private var questions: List<Question>? = null
    private var total: Int = 0
    private var currentQuestion: Int = 0

    override fun setSurvey(survey: Survey) {
        questions = survey.questions
        total = questions!!.size
        view?.updateProgress(0, total)
    }

    override fun start() {
        view?.showQuestion(questions!![currentQuestion])
    }

    private fun next() {
        if (currentQuestion < total) {
            view?.updateProgress(++currentQuestion, total)
        }
        if (currentQuestion >= total) {
            view?.finalize()
            return
        }
        view?.loadQuestion(questions!![currentQuestion])
    }

    override fun answer(answer: Answer) {
        next()
    }

    override fun answer(answer: String) {
        next()
    }
}