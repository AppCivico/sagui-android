package com.eokoe.sagui.features.survey

import com.eokoe.sagui.data.entities.Answer
import com.eokoe.sagui.data.entities.Question
import com.eokoe.sagui.data.entities.Survey
import com.eokoe.sagui.features.base.presenter.BasePresenter

/**
 * @author Pedro Silva
 */
interface SurveyContract {
    interface View {
        fun showQuestion(question: Question)
        fun hideQuestions()
        fun loadQuestion(question: Question)
        fun updateProgress(index: Int, size: Int)
        fun finalize()
    }

    interface Presenter : BasePresenter<View> {
        fun setSurvey(survey: Survey)
        fun start()
        fun answer(answer: Answer)
        fun answer(answer: String)
    }
}