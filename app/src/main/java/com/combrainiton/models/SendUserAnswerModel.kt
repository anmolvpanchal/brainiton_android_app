@file:Suppress("MemberVisibilityCanBePrivate")

package com.combrainiton.models

/**
 * Created by Dipendra Sharma  on 26-10-2018.
 */
class SendUserAnswerModel(quiz_id: Int, option_id: Int, quiz_pin: String, question_id: Int, time_remaining: Int, question_time: Int) {

    var quiz_id: Int = 0
    var option_id: Int = 0
    var quiz_pin: String = ""
    var question_id: Int = 0
    var time_remaining: Int = 0
    var question_time: Int = 0

    init {
        this.quiz_id = quiz_id
        this.question_id = question_id
        this.quiz_pin = quiz_pin
        this.option_id = option_id
        this.time_remaining = time_remaining
        this.question_time = question_time
    }


}