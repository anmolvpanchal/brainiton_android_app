package com.combrainiton.models

import java.io.Serializable

/**
 * Created by Dipendra Sharma  on 05-11-2018.
 */

class GetAllQuizResponceModel : Serializable {

    var quizzes: List<Allquizzes>? = null
    var featured_quizzes: List<Allquizzes>? = null
    var categories: List<CategoryList>? = null

    class Allquizzes(quiz_id: Int, quiz_title: String, total_questions: Int, category_id: Int, category_name: String, description: String, host_name: String, image_url: String?) : Serializable {

        var quiz_id: Int = 0
        var quiz_title: String = ""
        var total_questions: Int = 0
        var category_id: Int = 0
        var category_name: String = ""
        var description: String = ""
        var host_name: String = "No Data"
        var image_url: String? = ""

        init {
            this.quiz_id = quiz_id
            this.quiz_title = quiz_title
            this.total_questions = total_questions
            this.category_id = category_id
            this.category_name = category_name
            this.description = description
            this.host_name = host_name
            this.image_url = image_url
        }

    }

    class CategoryList(category_id: Int, category_name: String) : Serializable {
        var category_id: Int = 0
        var category_name: String = ""

        init {
            this.category_id = category_id
            this.category_name = category_name
        }

    }

}