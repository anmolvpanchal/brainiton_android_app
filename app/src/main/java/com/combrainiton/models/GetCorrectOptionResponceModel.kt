package com.combrainiton.models

/**
 * Created by Dipendra Sharma  on 29-10-2018.
 */
class GetCorrectOptionResponceModel {
    //all below object will call in case of online game quiz
    var quiz_pin: String = ""
    var quiz_id: Int = 0
    var question_id: Int = 0
    var optionId: Int = 0
    var question: QuestionDetail? = null
    //this var will be use in case of normal quiz
    var correct_answer_id: Int = 0
    var correct_answer_value: String = ""
    var score: Double = 0.0000
    var is_answer: Boolean = false

    class QuestionDetail {
        var options: List<OptionDetail>? = null
    }

    class OptionDetail {
        var option_id: Int = 0
        var is_answer: Boolean = false
        var option1_count: Int = 0
    }

}