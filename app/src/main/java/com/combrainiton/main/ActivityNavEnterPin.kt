package com.combrainiton.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.combrainiton.R
import com.combrainiton.managers.LivePollAndQuizManagement
import com.combrainiton.managers.NormalQuizManagement
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.AppSharedPreference
import com.combrainiton.utils.NetworkHandler
import kotlinx.android.synthetic.main.activity_nav_enter_pin.*


open class ActivityNavEnterPin : AppCompatActivity(), View.OnClickListener {

    private var gamePinStr: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_enter_pin)
        AppSharedPreference(this@ActivityNavEnterPin).saveInt("userLastScore", 0)
        //set on click listener for the start button
        activity_enter_pin_start_button.setOnClickListener(this@ActivityNavEnterPin)
        if (intent.hasExtra("QUIZ_ID")) {
            val quizID = "N"+intent.getStringExtra("QUIZ_ID").toString()
            activity_enter_pin_text_view.setText(quizID)
            activity_enter_pin_start_button.performClick()
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.activity_enter_pin_start_button -> { //on click of start button
                getGamePin() //get user entered pin
                if (isValidInpput()) { //if pin is valid
                    if (NetworkHandler(this@ActivityNavEnterPin).isNetworkAvailable()) { //check for network
                        verifyPinRequest() //verify the request
                    } else {
                        //show error message
                        Toast.makeText(this@ActivityNavEnterPin, resources.getString(R.string.error_network_issue), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun getGamePin() {
        gamePinStr = activity_enter_pin_text_view.text.toString().trim()
    }

    //check whether the entered input is empty or not
    private fun isValidInpput(): Boolean {
        return if (gamePinStr.isEmpty()) {
            Toast.makeText(this@ActivityNavEnterPin, resources.getString(R.string.error_wrong_pin), Toast.LENGTH_LONG).show()
            false
        } else {
            true
        }
    }

    private fun verifyPinRequest() {

        //nav_explore_progress_bar.visibility = View.VISIBLE
        val mDialog = AppProgressDialog(this@ActivityNavEnterPin)
        mDialog.show()
        //here we will check this pin is for poll or seminar quiz if user enter pin content "P" then it will consider as poll pin & we call other api for verify pin
        when (gamePinStr[0].toString()) {
            //if pin starts with p it's a poll
            "P" -> LivePollAndQuizManagement(this@ActivityNavEnterPin, this@ActivityNavEnterPin, mDialog).verifyPollPin(gamePinStr)
            //if a pin starts with n its a normal quiz
            "N" -> {
                val quizIdAsPin = gamePinStr.substring(1) //get he quiz id after removing n
                NormalQuizManagement(this@ActivityNavEnterPin, this@ActivityNavEnterPin, mDialog).getQuizDetail(quizIdAsPin.toInt())
            }
            else -> LivePollAndQuizManagement(this@ActivityNavEnterPin, this@ActivityNavEnterPin, mDialog).verifyQuizPin(gamePinStr)
        }
    }

    // this will open the home activtiy
    private fun explore() {
        if (NetworkHandler(this@ActivityNavEnterPin).isNetworkAvailable()) { //if internet is connected
            val mDialog = AppProgressDialog(this@ActivityNavEnterPin)
            mDialog.show() //show progress dialog
            //this will open the home activity after retriving the data
            NormalQuizManagement(this@ActivityNavEnterPin, this@ActivityNavEnterPin, mDialog).getAllQuiz()
        } else {
            //display error message
            Toast.makeText(this@ActivityNavEnterPin, resources.getString(R.string.error_network_issue), Toast.LENGTH_LONG).show()
        }

    }

    //open home activtiy on backpressed
    override fun onBackPressed() {
        explore()
    }

}
