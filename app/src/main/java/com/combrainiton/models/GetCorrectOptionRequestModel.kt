@file:Suppress("MemberVisibilityCanBePrivate")

package com.combrainiton.models

/**
 * Created by Dipendra Sharma  on 29-10-2018.
 */
class GetCorrectOptionRequestModel(quiz_pin: String, quiz_id: Int, question_id: Int, option1_id: Int, option2_id: Int, option3_id: Int, option4_id: Int) {

    var quiz_pin = ""
    var quiz_id: Int = 0
    var question_id: Int = 0
    var option1_id: Int = 0
    var option2_id: Int = 0
    var option3_id: Int = 0
    var option4_id: Int = 0

    init {
        this.quiz_pin = quiz_pin
        this.quiz_id = quiz_id
        this.question_id = question_id
        this.option1_id = option1_id
        this.option2_id = option2_id
        this.option3_id = option3_id
        this.option4_id = option4_id
    }

}