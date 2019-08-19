@file:Suppress("MemberVisibilityCanBePrivate")

package com.combrainiton.models

/**
 * Created by Dipendra Sharma  on 27-11-2018.
 */
class SendPollUserAnswerRequestModel(poll_id: Int, question_id: Int, poll_pin: String, option_id: Int) {

    var poll_id: Int = 0
    var question_id: Int = 0
    var poll_pin: String = ""
    var option_id: Int = 0

    init {
        this.poll_id = poll_id
        this.question_id = question_id
        this.poll_pin = poll_pin
        this.option_id = option_id
    }

}