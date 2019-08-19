package com.combrainiton.models

import java.io.Serializable

/**
 * Created by Dipendra Sharma  on 08-12-2018.
 */
class QuizDetailResponceModel : Serializable {

    var host_name: String = ""
    var quiz_id: Int = 0
    var quiz_title: String = ""
    var description: String = ""
    var total_questions: Int = 0
    var image_url: String = ""

}