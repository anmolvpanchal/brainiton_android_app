package com.combrainiton.models

import java.io.Serializable

/**
 * Created by Dipendra Sharma  on 05-12-2018.
 */

class GetUserRecentAllQuizResponceModel : Serializable {

    var status: Int = 0
    var message: String = ""
    var quizzes: ArrayList<RecentQuizList>? = null

    class RecentQuizList : Serializable {
        var id: Int = 0
        var name: String = ""
        var description: String = ""
        var image: String = ""
        var host_name: String = ""
    }

}