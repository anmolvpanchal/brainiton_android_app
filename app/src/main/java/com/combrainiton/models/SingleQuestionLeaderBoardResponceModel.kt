package com.combrainiton.models

import java.io.Serializable

/**
 * Created by Dipendra Sharma  on 17-11-2018.
 */
class SingleQuestionLeaderBoardResponceModel : Serializable {

    var users: ArrayList<UserList>? = null

    class UserList : Serializable {
        var is_answer: Boolean = false
        var name: String = ""
        var score: Int = 0
        var api_token: String = ""
        var total_score: Int = 0
    }

}