package com.combrainiton.models

import java.io.Serializable

/**
 * Created by Dipendra Sharma  on 31-10-2018.
 */
class GetNormalQuestionListResponceModel : Serializable {
    var quiz_id: Int = 0
    var questions: ArrayList<QuestionResponceModel>? = null
}

