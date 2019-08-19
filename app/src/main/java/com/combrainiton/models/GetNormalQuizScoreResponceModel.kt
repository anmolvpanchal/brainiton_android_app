package com.combrainiton.models

import java.io.Serializable

/**
 * Created by Dipendra Sharma  on 01-11-2018.
 */
class GetNormalQuizScoreResponceModel : Serializable {

    var quiz_id: Int = 0
    var touranment_id: Int = 0
    var correct_answers: Int = 0
    var wrong_answers: Int = 0
    var total_attemted_questions: Int = 0
    var total_questions: Int = 0
    var total_score: Double = 0.00

}