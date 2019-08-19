package com.combrainiton.models

/**
 * Created by Dipendra Sharma  on 26-11-2018.
 */
class GetPollOptionStatisticsResponceModel {

    var poll_pin: String = ""
    var poll_id: String = ""
    var question_id: Int = 0
    var question: QuestionDetail? = null

    class QuestionDetail {
        var options: ArrayList<OptionList>? = null
    }

    class OptionList {
        var option_id: Int = 0
        var option_count: Float = 0.0F
    }


}