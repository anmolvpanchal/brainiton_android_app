package com.combrainiton.fragments

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.combrainiton.R
import com.combrainiton.adaptors.AdapterCourseLesson
import com.combrainiton.adaptors.AdaptorSearchResultList
import com.combrainiton.api.ApiClient
import com.combrainiton.api.ApiErrorParser
import com.combrainiton.authentication.ActivitySignIn
import com.combrainiton.main.ActivityNavExplore
import com.combrainiton.managers.NormalQuizManagement
import com.combrainiton.managers.NormalQuizManagementInterface
import com.combrainiton.models.CommonResponceModel
import com.combrainiton.models.GetAllQuizResponceModel
import com.combrainiton.utils.AppAlerts
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.AppSharedPreference
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.activity_nav_search.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CourseLessonsFragment : androidx.fragment.app.Fragment() {

    private lateinit var quizList: ArrayList<GetAllQuizResponceModel.Allquizzes> //list for all the quizzes
    private lateinit var mSearchAdapter: AdapterCourseLesson //adapter for search results
    lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private var requestInterface: NormalQuizManagementInterface? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_course_lessons,container,false)

        //quizList = response.body()!!.quizzes as ArrayList<GetAllQuizResponceModel.Allquizzes>?

        /*val mDialog = AppProgressDialog(context!!)
        mDialog.show()

        NormalQuizManagement(context!!, activity!!, mDialog).getAllQuiz()*/

        initView()

        recyclerView = view.findViewById(R.id.course_lessonsRecyclerView)

        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

        return view
    }

    //For getting quizes from API
    private fun initView() {

        requestInterface = ApiClient.getClient().create(NormalQuizManagementInterface::class.java)
        val getQuizCall: Call<GetAllQuizResponceModel>? = requestInterface!!.getAllQuiz()

        getQuizCall!!.enqueue(object : Callback<GetAllQuizResponceModel> {

            //called when http request fails
            override fun onFailure(call: Call<GetAllQuizResponceModel>, t: Throwable) {
                //mProgressDialog.dialog.dismiss()
                AppAlerts().showAlertMessage(context!!, "Error", context!!.resources.getString(R.string.error_server_problem))
            }

            //called when http response is recieved
            override fun onResponse(call: Call<GetAllQuizResponceModel>, response: Response<GetAllQuizResponceModel>) {
                //TODO after adding the progress bar replace it with relevant statement to hide or dismiss your new progress bar
                //DO NOT DELETE this commented line anywhere from the project
                //mProgressDialog.dialog.dismiss()
                if (response.isSuccessful) { //if response from the backend is recieved\

                    //parse the JSON object quizzes as array list of GetAllQuizResponceModel.AllQuizzes
                    val quizList: ArrayList<GetAllQuizResponceModel.Allquizzes> = response.body()!!.quizzes as ArrayList<GetAllQuizResponceModel.Allquizzes>
                    //parse the JSON object featured_quizzes as array list of GetAllQuizResponceModel.AllQuizzes
                    val featuredQuizzes: ArrayList<GetAllQuizResponceModel.Allquizzes>? = response.body()!!.featured_quizzes as ArrayList<GetAllQuizResponceModel.Allquizzes>?
                    //parse the JSON object categories as array list of GetAllQuizResponceModel.CategoryList
                    val categoryList: ArrayList<GetAllQuizResponceModel.CategoryList>? = response.body()!!.categories as ArrayList<GetAllQuizResponceModel.CategoryList>?
                    categoryList!!.add(GetAllQuizResponceModel.CategoryList(0, "quizzes",0))

                    //TODO this should be taken care of at the backend side remove it after confirmation
                    //redudant code to replace null values from http response
                    /*            for (i in quizList!!.indices) {
                                    if (quizList[i].category_id == null || quizList[i].category_name == null) {
                                        val getData = quizList[i]
                                        var setItemModel: GetAllQuizResponceModel.Allquizzes?
                                        if (quizList[i].host_name == null) {
                                            setItemModel = GetAllQuizResponceModel.Allquizzes(getData.quiz_id, getData.quiz_title, getData.total_questions, 0, "quizzes", getData.description, "No Data", getData.image_url)
                                        } else {
                                            setItemModel = GetAllQuizResponceModel.Allquizzes(getData.quiz_id, getData.quiz_title, getData.total_questions, 0, "quizzes", getData.description, getData.host_name, getData.image_url)
                                        }
                                        quizList.set(i, setItemModel)

                                    }

                                }
                                for (i in quizList.indices) {
                                    if (quizList[i].host_name == null) {
                                        var getData = quizList[i]
                                        var setItemModel = GetAllQuizResponceModel.Allquizzes(getData.quiz_id, getData.quiz_title, getData.total_questions, getData.category_id, getData.category_name, getData.description, "No Data", getData.image_url)
                                        quizList.set(i, setItemModel)

                                    }

                                }
                                for (i in featured_quizzes!!.indices) {
                                    if (featured_quizzes[i].host_name == null) {
                                        val getData = featured_quizzes[i]
                                        val setItemModel = GetAllQuizResponceModel.Allquizzes(getData.quiz_id, getData.quiz_title, getData.total_questions, 0, "", getData.description, "No Data", getData.image_url)
                                        featured_quizzes.set(i, setItemModel)
                                    }
                                }
            */
                    //This will goto adapter
                    getData(quizList)


                } else {
                    //if the response is not successfull then show the error
                    val errorMsgModle: CommonResponceModel = ApiErrorParser().errorResponce(response)
                    isSessionExpire(errorMsgModle)
                }
            }
        })


    }

    private fun isSessionExpire(errorMsgModle: CommonResponceModel) {
        //if error message status is equal to 404
        if (errorMsgModle.status == 404) {

            //show the message to user
            Toast.makeText(context, "Your Session Is Expire. Login Again For Continue.", Toast.LENGTH_LONG).show()

            //delete all shared preferences
            AppSharedPreference(context!!).deleteSharedPreference()

            //start login activity
            activity!!.startActivity(Intent(context, ActivitySignIn::class.java)
                    .putExtra("from", "fail"))//add set from key value to "fail"

            //finish current activity
            activity!!.finish()

        } else {
            //display other message
            AppAlerts().showAlertMessage(context!!, "Error", errorMsgModle.message)
        }
    }

    private fun getCategoryData(id: Int,quizList: ArrayList<GetAllQuizResponceModel.Allquizzes>): ArrayList<GetAllQuizResponceModel.Allquizzes> {
        val categoryQuizData = ArrayList<GetAllQuizResponceModel.Allquizzes>() //get all quiz from all quiz respoonse model
        for (i in quizList.indices) {   // for each object of quiz in the list
            val categoryId = quizList[i].category_id  // get the cateory id of that object
            if (categoryId == id) { // of category id euals to the var "id" (the id passed as argument)
                categoryQuizData.add(quizList[i]) //add that quiz object into the categoryQuizData list
            }
        }
        return categoryQuizData // return the list
    }

    private fun getData(quizList: ArrayList<GetAllQuizResponceModel.Allquizzes>){

        val quizDataList = getCategoryData(28,quizList)

        //attach search result adapter to search result recycvler view
        mSearchAdapter = AdapterCourseLesson(context!!, activity!!, quizDataList)
        recyclerView.adapter = mSearchAdapter
    }

}
