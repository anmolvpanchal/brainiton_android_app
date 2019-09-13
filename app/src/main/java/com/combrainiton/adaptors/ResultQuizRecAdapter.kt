package com.combrainiton.adaptors

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.combrainiton.R
import com.combrainiton.api.ApiClient
import com.combrainiton.api.ApiErrorParser
import com.combrainiton.managers.NormalQuizManagementInterface
import com.combrainiton.models.CommonResponceModel
import com.combrainiton.models.GetNormalQuestionListResponceModel
import com.combrainiton.models.QuestionResponceModel
import com.combrainiton.normalQuiz.ActivityNormalQuizResult
import com.combrainiton.utils.AppAlerts
import com.combrainiton.utils.AppSharedPreference
import kotlinx.android.synthetic.main.normal_quiz_result_cell.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResultQuizRecAdapter(val context: ActivityNormalQuizResult, var questionsList: ArrayList<QuestionResponceModel>, val quizId: Int, val quizName: String) : RecyclerView.Adapter<ResultQuizRecAdapter.ViewHolder>() {

    private val TAG = "ResultQuizRecAdapter"
    private var requestInterface: NormalQuizManagementInterface? = null
    private var currentQuestion: Int = 0



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.normal_quiz_result_cell, parent, false))
    }

    override fun getItemCount(): Int {
        return questionsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        getQuestions(quizId, quizName)

        holder?.recyclerQuestionText?.text = questionsList.get(position).toString()
        Log.e(TAG,"aayo ${questionsList.size.toString()}")


    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val recyclerQuestionText = view.question_layout
        val questionNoTeaxt = view.result_Cell_questionNo_text
        val leftButton = view.result_Cell_left_button
        val rightButton = view.result_Cell_right_button
    }

    fun getQuestions(quizId: Int,quizName: String) {

        //create api client first
        val apiToken: String = AppSharedPreference(context).getString("apiToken")

        //intialize the normal quiz management interface
        requestInterface = ApiClient.getClient(apiToken).create(NormalQuizManagementInterface::class.java)

        //attach your get method with call object
        val getQuestionCall: Call<GetNormalQuestionListResponceModel>? = requestInterface!!.getQuestions(quizId)

        //request the data from backend
        getQuestionCall!!.enqueue(object : Callback<GetNormalQuestionListResponceModel> {

            //on request fail
            override fun onFailure(call: Call<GetNormalQuestionListResponceModel>, t: Throwable) {
                //mProgressDialog.dialog.dismiss()
                AppAlerts().showAlertMessage(context, "Error", context.resources.getString(R.string.error_server_problem))

            }

            //on response recieved
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<GetNormalQuestionListResponceModel>, response: Response<GetNormalQuestionListResponceModel>) {
                //mProgressDialog.dialog.dismiss()
                if (response.isSuccessful) {

                    //get JSON object questions as array list of QuestionResponseModel
                    questionsList = response.body()!!.questions!!
                    var questionModel : QuestionResponceModel
                    questionModel = questionsList!![currentQuestion]
                    Log.e(TAG,"questions $questionsList")
                    Log.e(TAG,"question list size ${questionsList?.size.toString()}")
                    Log.e(TAG,"question" + questionModel.question_title.subSequence(0,questionModel.question_title.indexOf(";")) )

                    //get JSON object quiz id as integer from response
                    val qid = response.body()!!.quiz_id

                } else {
                    //if the response is not successfull then show the error

                    val errorMsgModle: CommonResponceModel = ApiErrorParser().errorResponce(response)
                }
            }
        })

    }

}
