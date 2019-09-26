package com.combrainiton.models

import java.io.Serializable

/**
 * Created by Dipendra Sharma  on 24-10-2018.
 */
class QuestionResponceModel : Serializable {

    var question_title: String = ""
    var question_time: Int = 0
    var question_id: Int = 0
    var question_image: String = ""
    var question_description: String = ""
    var options: List<OptionListModel>? = null

    class OptionListModel : Serializable {
        var option_title: String = ""
        var option_id: Int = 0

    }

}