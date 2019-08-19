package com.combrainiton.models

/**
 * Created by Dipendra Sharma  on 01-11-2018.
 */
class GetNormalQuizScoreRequestModel {

    var quiz_id: Int = 0
    var questions: ArrayList<QuestionsList>? = null

    class QuestionsList(question_id: Int, option_id: Int) {

        var question_id: Int = 0
        var option_id: Int = 0

        init {
            this.question_id = question_id
            this.option_id = option_id
        }
    }

}