package com.combrainiton.adaptors

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.combrainiton.R
import com.combrainiton.normalQuiz.ActivityNormalQuizDescription
import com.combrainiton.normalQuiz.ActivityNormalQuizInstruction
import com.combrainiton.subscription.LessonsDataList_API
import com.combrainiton.subscription.ServiceGenerator
import com.combrainiton.subscription.SubscriptionDataList_API
import com.combrainiton.subscription.SubscriptionInterface
import com.combrainiton.utils.AppAlerts
import com.combrainiton.utils.AppSharedPreference
import kotlinx.android.synthetic.main.course_lessons_card_view_item.view.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdapterCourseLesson(var mContext: Context, var mActivity: Activity, val lessonsDataList: ArrayList<LessonsDataList_API>, val subscriptionDataList: ArrayList<SubscriptionDataList_API>) : androidx.recyclerview.widget.RecyclerView.Adapter<AdapterCourseLesson.MyViewHolder>() {

    var quiz_id : String = ""



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        return MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.course_lessons_card_view_item, parent, false))
    }

    override fun getItemCount(): Int {
        return lessonsDataList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(mContext)
                .load(lessonsDataList[position].quizImage)
                .into(holder.imgQuiz)
        holder.tvQuizTitle.text = lessonsDataList[position].lessonName
        holder.tvQuizHost.text = "By Unknown"
        holder.cvMain.tag = position

        //Setting lesson count
        val no = position + 1
        holder.lessonCount.text = no.toString()

       holder.Card.setOnClickListener {

           val lessonIDtoPass = lessonsDataList.get(position).lessonId
           val quiz_name = lessonsDataList.get(position).lessonName.toString()
           getLessonsFromApi(lessonIDtoPass.toString(),quiz_name)
       }

        //Removing UpperLine and LowerLine from first and last card
        /*if(position == 0){ //First card
            holder.upperLine.visibility = View.INVISIBLE
        } else if(position == itemCount - 1){ //Last card
            holder.lowerLine.visibility = View.INVISIBLE
        }*/

        /*holder.cvMain.setOnClickListener { p0 ->

            val selectedPosition: Int = p0!!.tag as Int
            if (filterList!![selectedPosition].total_questions != 0) {
                mActivity.startActivity(Intent(mContext, ActivityNormalQuizDescription::class.java)
                        .putExtra("quizId", filterList!![selectedPosition].quiz_id)
                        .putExtra("description", filterList!![selectedPosition].description)
                        .putExtra("totalQuestion", filterList!![selectedPosition].total_questions)
                        .putExtra("quizName", filterList!![selectedPosition].quiz_title)
                        .putExtra("hostName", filterList!![selectedPosition].host_name)
                        .putExtra("image", filterList!![selectedPosition].image_url))
                mActivity.finish()
            } else {
                Toast.makeText(mContext, "There Is no Questions Added By Host", Toast.LENGTH_LONG).show()
            }
        }*/
    }

    class MyViewHolder(mView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mView) {

        val imgQuiz : ImageView
        val tvQuizTitle : TextView
        val tvQuizHost : TextView
        val cvMain : LinearLayout
        val lessonCount : TextView
        val upperLine : View
        val lowerLine : View
        val Card : CardView


        init {
             imgQuiz = mView.course_lessons_quiz_image!!
             tvQuizTitle = mView.course_lessons_quiz_name!!
             tvQuizHost = mView.course_lessons_quiz_sponsor!!
             cvMain = mView.course_lessons_Container!!
             lessonCount = mView.course_lessons_lessons_count
             upperLine = mView.course_lessons_upper_line
             lowerLine = mView.course_lessons_lower_line
             Card = mView.my_quizzes_list_item_main_container

        }
    }

    fun getLessonsFromApi(lessonID: String, quizName: String) {

        //create api client first
        val apiToken: String = AppSharedPreference(mContext).getString("apiToken")

        val apiClient = ServiceGenerator.getClient(apiToken).create(SubscriptionInterface::class.java)
        val call = apiClient.getQuizID(lessonID.toInt())

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                //mProgressDialog.dialog.dismiss()
                AppAlerts().showAlertMessage(mContext, "Error", mActivity.resources.getString(R.string.error_server_problem))
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if (!response.isSuccessful()) {
                    Toast.makeText(mContext, "Something went Wrong !!" + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("!response.isSuccessful", "body \n"
                            + response.errorBody().toString()
                            + " code ${response.code()}")
                    return;
                }

                try {

                    val resp = response.body()?.string()
                    val rootObj = JSONObject(resp)
                    val play = rootObj.getString("play")

                    if (play.equals("false")){
                        Toast.makeText(mContext, "Quiz Will be available tomorrow " , Toast.LENGTH_LONG).show();
                    }else{
                         quiz_id = rootObj.getString("quiz_id")

                        Log.e("AdapterResult","responce" + quiz_id)

                        // getting data and then intent
                        gettingDetailsToPass(quiz_id.toInt())

                    }


                } catch (ex: Exception) {
                    when (ex) {
                        is IllegalAccessException, is IndexOutOfBoundsException -> {
                            Log.e("catch block", "some known exception" + ex)
                        }
                        else -> Log.e("catch block", "other type of exception" + ex)

                    }

                }
            }

        })


    }

    fun gettingDetailsToPass(QuizID : Int) {

        //create api client first
        val apiToken: String = AppSharedPreference(mContext).getString("apiToken")

        val apiClient = ServiceGenerator.getClient(apiToken).create(SubscriptionInterface::class.java)
        val call = apiClient.getQuizDetailForSubs(QuizID)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                //mProgressDialog.dialog.dismiss()
                AppAlerts().showAlertMessage(mContext, "Error", mActivity.resources.getString(R.string.error_server_problem))
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if (!response.isSuccessful()) {
                    Toast.makeText(mContext, "Something went Wrong !!" + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("!response.isSuccessful", "body \n"
                            + response.errorBody().toString()
                            + " code ${response.code()}")
                    return;
                }

                try {

                    val resp = response.body()?.string()
                    val rootObj = JSONObject(resp)
                    val host_name = rootObj.getString("host_name")
                    val description = rootObj.getString("description")
                    val quiz_title = rootObj.getString("quiz_title")
                    val quiz_id = rootObj.getString("quiz_id")
                    val image_url = rootObj.getString("image_url")
                    val total_questions = rootObj.getString("total_questions")

                    Log.i("fromcourse frag Id",quiz_id.toString())


                    mActivity.startActivity(Intent(mActivity, ActivityNormalQuizDescription::class.java)
                            .putExtra("quizId", quiz_id.toInt()) //pass quiz id
                            .putExtra("quizName", quiz_title)
                            .putExtra("totalQuestion", total_questions)
                            .putExtra("hostName", host_name)
                            .putExtra("description", description)
                            .putExtra("image", image_url)) //pass quiz name



                } catch (ex: Exception) {
                    when (ex) {
                        is IllegalAccessException, is IndexOutOfBoundsException -> {
                            Log.e("catch block", "some known exception" + ex)
                        }
                        else -> Log.e("catch block", "other type of exception" + ex)

                    }

                }
            }

        })


    }

}