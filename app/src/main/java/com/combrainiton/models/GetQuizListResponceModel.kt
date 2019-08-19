package com.combrainiton.models

import java.io.Serializable

/**
 * Created by Dipendra Sharma  on 29-10-2018.
 */
class GetQuizListResponceModel(quiz_id:Int,quiz_title:String,description:String):Serializable{
    var quiz_id:Int=0
    var quiz_title:String=""
    var description:String=""
init {
    this.quiz_id=quiz_id
    this.quiz_title=quiz_title
    this.description=description
}

}