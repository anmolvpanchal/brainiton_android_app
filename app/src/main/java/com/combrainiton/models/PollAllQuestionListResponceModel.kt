package com.combrainiton.models

import java.io.Serializable

/**
 * Created by Dipendra Sharma  on 27-11-2018.
 */
class PollAllQuestionListResponceModel : Serializable {

    var status: Int = 0
    var poll_id: Int = 0
    var poll_name: String = ""
    var questions: ArrayList<QuestionsList>? = null

    class QuestionsList : Serializable {
        var question_id: Int = 0
        var question_text: String = ""
        var options: ArrayList<OptionsList>? = null
    }

    class OptionsList : Serializable {
        var option_id: Int = 0
        var option: String = ""
        var option_percent: Float = 0f
    }

}