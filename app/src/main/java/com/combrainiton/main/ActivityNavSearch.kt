@file:Suppress("UNCHECKED_CAST")

package com.combrainiton.main

import android.os.Bundle
import androidx.transition.Slide
import androidx.transition.TransitionManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.combrainiton.R
import com.combrainiton.adaptors.AdaptorSearchSuggestionList
import com.combrainiton.adaptors.AdaptorSearchResultList
import com.combrainiton.models.GetAllQuizResponceModel
import com.combrainiton.managers.NormalQuizManagement
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.NetworkHandler
import kotlinx.android.synthetic.main.activity_nav_search.*


class ActivityNavSearch : AppCompatActivity(), View.OnClickListener, TextWatcher {

    private lateinit var searchFrom: String //stores the key "from" which stores the location from which the button was clicked
    private lateinit var mSearchAdapter: AdaptorSearchResultList //adapter for search results

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_search)

        //initialise the main view
        initMainView()

    }

    //initialize the main view
    private fun initMainView() {

        //first we have to check user coming from category search or all quiz search
        //get the value of key "from" from intent extras
        searchFrom = intent.getStringExtra("from")
        if (searchFrom == "category") { //when user has clicked on category list item

            //initialise views to show a specific category by default
            initForCategory()
            //and se the title as the name of category
            nav_search_category_title.text = intent.getStringExtra("categoryName")

        } else { //when user has clicked on search button from explore activity's top bar

            initForHomeSearch() //initialise view to show all quizzes by default

        }

        //get all featured quizzes 
        val quizDataList = intent.getSerializableExtra("quizData") as ArrayList<GetAllQuizResponceModel.Allquizzes>?

        //set horizontal linear layout manager to the search suggestion recycler view
        nav_search_suggestion_recycler_view.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@ActivityNavSearch, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)

        //attach the adaptor to the search suggestion recycler view
        nav_search_suggestion_recycler_view.adapter = AdaptorSearchSuggestionList(this@ActivityNavSearch, this@ActivityNavSearch, quizDataList!!)

        //set vertical linear layout manager to the search result recycler view
        nav_search_result_recyler_view.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@ActivityNavSearch)

        //attach search result adapter to search result recycvler view
        mSearchAdapter = AdaptorSearchResultList(this@ActivityNavSearch, this@ActivityNavSearch, quizDataList)
        nav_search_result_recyler_view.adapter = mSearchAdapter

        //text change listener for edit text in search bar
        nav_serarch_edit_text_for_search.addTextChangedListener(this@ActivityNavSearch)
        nav_serarch_edit_text_for_search.setImeOptions(EditorInfo.IME_ACTION_DONE)

        //onclick listener to close button in search bar
        nav_search_close_button.setOnClickListener(this@ActivityNavSearch)
    }

    //this method will be called when user is searching from home
    private fun initForHomeSearch() {

        //turn on search bar visibility
        nav_search_card_view_search_box.visibility = View.VISIBLE

        //turn off category bar visibility
        nav_search_category_title_bar.visibility = View.GONE

    }

    // this method will be called when user is searching from category
    private fun initForCategory() {

        //turn off search bar visibility
        nav_search_card_view_search_box.visibility = View.GONE

        //turn on category bar visibility
        nav_search_category_title_bar.visibility = View.VISIBLE

        //set on click listener to top bar search button
        nav_search_search_button.setOnClickListener {
            openSearchBox(nav_search_top_bar, nav_search_card_view_search_box)
            //turn off category bar visibility
            nav_search_category_title_bar.visibility = View.GONE
        }

        nav_search_close_button.setOnClickListener {
            closeSearchBox(nav_search_card_view_search_box, nav_search_card_view_search_box)
            //turn off category bar visibility
            nav_search_category_title_bar.visibility = View.VISIBLE
        }

    }

    //open search box with animation
    private fun openSearchBox(viewGroup: ViewGroup, view: View) {
        val transition = Slide(Gravity.END) //set transition
        transition.duration = 500 //set delay
        transition.addTarget(view.id) //add target view
        TransitionManager.beginDelayedTransition(viewGroup, transition) //start transition
        view.visibility = View.VISIBLE //set view visibility to visible make it visible only after starting the transition
    }

    //close search box with animation
    private fun closeSearchBox(viewGroup: ViewGroup, view: View) {
        val transition = Slide(Gravity.END) //set transition
        transition.duration = 500 //set delay
        transition.addTarget(view.id) //set target view
        TransitionManager.beginDelayedTransition(viewGroup, transition) //start transition
        view.visibility = View.INVISIBLE //set view visibility to invisible make it invisible only after starting the transition
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.nav_search_close_button -> {
                if (searchFrom == "home") { //if "from" key equals to home
                    explore() //go back to home activtiy
                } else if (searchFrom == "category") { //if "from" key equals to cateory
                    initForCategory() //re-initialize the view loading specific category related quizzes
                }
            }
        }
    }

    //do nothing
    override fun afterTextChanged(p0: Editable?) {
        //do nothing
    }

    //do nothing
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        //do nothing 
    }

    //this will change the filter of adapter as per the user entered string
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        val userInput = p0.toString() //get user entered string
        mSearchAdapter.filter.filter(userInput) //set user entered string as the filter of the search adapter 
    }

    // this will open the home activtiy
    private fun explore() {
        if (NetworkHandler(this@ActivityNavSearch).isNetworkAvailable()) { //if internet is connected
            //{we are passing this object only to satisfy parameters request}
            //{this progress bas has no effect all of it's method have been commented}
            //go to the progress bar class for further information
            val mDialog = AppProgressDialog(this@ActivityNavSearch)
            mDialog.show() //show progress dialog
            //this will open the home activity after retriving the data
            NormalQuizManagement(this@ActivityNavSearch, this@ActivityNavSearch, mDialog).getAllQuiz()
        } else {
            Toast.makeText(this@ActivityNavSearch, resources.getString(R.string.error_network_issue), Toast.LENGTH_LONG).show() //display error message
        }
    }

    //open home activtiy on backpressed
    override fun onBackPressed() {
        explore()
    }

}