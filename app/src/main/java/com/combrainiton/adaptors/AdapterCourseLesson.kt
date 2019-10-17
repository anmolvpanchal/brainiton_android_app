package com.combrainiton.adaptors

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.combrainiton.R
import com.combrainiton.normalQuiz.ActivityNormalQuizDescription
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


class AdapterCourseLesson(var mContext: Context, var mActivity: Activity, val lessonsDataList: ArrayList<LessonsDataList_API>, val subscriptionDataList: ArrayList<SubscriptionDataList_API>, val brandPosition: Int) : androidx.recyclerview.widget.RecyclerView.Adapter<AdapterCourseLesson.MyViewHolder>() {

    var quiz_id: String = ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        return MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.course_lessons_card_view_item, parent, false))
    }

    override fun getItemCount(): Int {
        return lessonsDataList.size
    }

    //Always use this two following methods in recyclerView adapter
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvQuizTitle.text = lessonsDataList[position].lessonName
        holder.tvQuizHost.text = "By Unknown"
        holder.cvMain.tag = position

        //Setting lesson count
        val no = position + 1
        holder.lessonCount.text = no.toString()

        //Remove first and last line from lesson list
        if(position == 0){
            holder.upperLine.visibility = View.INVISIBLE
        }
        if(position+1 == lessonsDataList.size){
            holder.lowerLine.visibility = View.INVISIBLE
        }

        //Should be locked if lesson is not unlocked
        if ((position + 1) > subscriptionDataList[brandPosition].currentLessonNumber!!.toInt()) {
            holder.imgQuiz.setBackgroundResource(R.drawable.lock)
            holder.imgQuiz.scaleType = ImageView.ScaleType.CENTER_INSIDE

            //Making the lines & circle red and lesson info view light orange if locked
            holder.upperLine.setBackgroundColor(mActivity.resources.getColor(R.color.currentLesson))
            holder.lowerLine.setBackgroundColor(mActivity.resources.getColor(R.color.currentLesson))
            holder.background.setBackgroundDrawable(mActivity.resources.getDrawable(R.drawable.current_lesson_circle_shape))
            holder.lessonInfo.setBackgroundColor(mActivity.resources.getColor(R.color.lessonInfoLocked))
        } else {
            Glide.with(mContext)
                    .load(lessonsDataList[position].quizImage)
                    .into(holder.imgQuiz)

            //Making the lines and circle green if already unlocked
            holder.upperLine.setBackgroundColor(mActivity.resources.getColor(R.color.colorCategoryFourDisabled))
            holder.lowerLine.setBackgroundColor(mActivity.resources.getColor(R.color.colorCategoryFourDisabled))
            holder.background.setBackgroundDrawable(mActivity.resources.getDrawable(R.drawable.green_circle_shape))
        }

        //Changing current lesson color to purple
        if(position+1 == subscriptionDataList[brandPosition].currentLessonNumber!!.toInt()){
            holder.upperLine.setBackgroundColor(mActivity.resources.getColor(R.color.lessonCountColor))
            holder.lowerLine.setBackgroundColor(mActivity.resources.getColor(R.color.lessonCountColor))
            holder.background.setBackgroundDrawable(mActivity.resources.getDrawable(R.drawable.circle_shape))
        }

        holder.Card.setOnClickListener {
            val lessonIDtoPass = lessonsDataList.get(position).lessonId
            getLessonsFromApi(lessonIDtoPass.toString(), position + 1, subscriptionDataList[brandPosition].lastLessonNumber!!.toInt())
        }
    }

    class MyViewHolder(mView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mView) {

        val imgQuiz: ImageView
        val tvQuizTitle: TextView
        val tvQuizHost: TextView
        val cvMain: LinearLayout
        val lessonCount: TextView
        val upperLine: View
        val lowerLine: View
        val Card: CardView
        val layout: LinearLayout
        val background: TextView
        val lessonInfo: RelativeLayout


        init {
            imgQuiz = mView.course_lessons_quiz_image!!
            tvQuizTitle = mView.course_lessons_quiz_name!!
            tvQuizHost = mView.course_lessons_quiz_sponsor!!
            cvMain = mView.course_lessons_Container!!
            lessonCount = mView.course_lessons_lessons_count
            upperLine = mView.course_lessons_upper_line
            lowerLine = mView.course_lessons_lower_line
            Card = mView.my_quizzes_list_item_main_container
            layout = mView.imageLayout
            background = mView.backgroundCircle
            lessonInfo = mView.lessonInfo
        }
    }

    fun getLessonsFromApi(lessonID: String, currentPosition: Int, lastLesson: Int) {

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
                    val days = (currentPosition - lastLesson) - 1

                    if (play.equals("false")) {
                        if (days == 0) {
                            Toast.makeText(mContext, "Quiz will unlock Tomorrow", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(mContext, "Quiz will be available after $days days", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        quiz_id = rootObj.getString("quiz_id")

                        Log.e("AdapterResult", "responce" + quiz_id)

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

    fun gettingDetailsToPass(QuizID: Int) {

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

                    Log.i("fromcourse frag Id", quiz_id.toString())


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