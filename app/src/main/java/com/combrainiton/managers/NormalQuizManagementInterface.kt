package com.combrainiton.managers

import com.combrainiton.models.*
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by Dipendra Sharma  on 31-10-2018.
 */
interface NormalQuizManagementInterface {
    //for getting all quiz list on home page
    @GET("api/quiz/all/")
    fun getAllQuiz(): Call<GetAllQuizResponceModel>

    //for getting question list by quiz id
    @GET("api/quiz/{quizId}/play/")
    fun getQuestions(@Path("quizId") quzId: Int): Call<GetNormalQuestionListResponceModel>

    //for getting right option id
    @Headers("Content-Type: application/json")
    @POST("api/quiz/question/answer/")
    fun getCorrectOption(@Body requestData: Map<String, Int>): Call<GetCorrectOptionResponceModel>

    //for getting final quiz user score
    @Headers("Content-Type: application/json")
    @POST("api/quiz/answers/")
    fun getQuizScore(@Body requestData: GetNormalQuizScoreRequestModel): Call<GetNormalQuizScoreResponceModel>

    @GET("api/player/quizzes/")
    fun getRecentQuiz(): Call<GetUserRecentAllQuizResponceModel>

    //for getting single quiz detail.
    @GET("api/quiz/detail/{quizid}/")
    fun getQuizDetail(@Path("quizid") quizId: Int): Call<QuizDetailResponceModel>


}