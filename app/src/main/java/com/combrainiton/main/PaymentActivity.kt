package com.combrainiton.main

import android.app.DatePickerDialog
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.combrainiton.R
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_payment.*
import java.lang.StringBuilder
import java.util.*
import java.util.regex.Pattern

class PaymentActivity : AppCompatActivity() {

    lateinit var paymentButton: Button
    lateinit var viewGroup: ViewGroup
    lateinit var builder: AlertDialog.Builder
    lateinit var alertDialog: AlertDialog
    lateinit var userAge: TextInputEditText
    lateinit var userName: TextInputEditText
    lateinit var userMobNo: TextInputEditText
    lateinit var userEmailId: TextInputEditText
    lateinit var userGender: RadioGroup
    lateinit var datePickerDialog: DatePickerDialog
    var selectedPlan : Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        //assigning ids
        paymentButton = findViewById(R.id.activity_payment_paymentButton)
        userAge = findViewById(R.id.activity_payment_userAge)
        userName =findViewById(R.id.activity_payment_userName)
        userEmailId =findViewById(R.id.activity_payment_userEmailId)
        userMobNo =findViewById(R.id.activity_payment_userPhoneNo)
        userGender =findViewById(R.id.activity_payment_genderRadioGroup)

        //getting selected plan amount and setting to textview

        selectedPlan = intent.getIntExtra("planSelected",2)
        Log.e("planSelected",selectedPlan.toString())
        amount_to_pay_text.text = "₹ "+selectedPlan.toString()


        paymentButton.setOnClickListener {
            if(checkAllValidationBeforeSubmit()){
                alertDialog()
                Log.i("payment","all true")
            }
        }

        //Validation
        userEmailId.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus){
                emailIdValidation()
            } else{
                emailIdValidation()
            }
        }

        userAge.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                Log.i("payment","in")
                datePicker()
            } else{
                userAgeValidation()
            }
        }
    }

    private fun checkAllValidationBeforeSubmit() : Boolean{

        var age:Boolean = false
        var name:Boolean = false
        var mobileNo:Boolean = false
        var emailId:Boolean = false

        //user age
        val pattern = Pattern.compile("(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)")
        if(!pattern.matcher(userAge.text).matches()){
            userAge.error = "Invalid date"
            age = false
        } else{
            age = true
        }

        //user mobile no
        if(userMobNo.text!!.trim().length < 10 || userMobNo.text!!.trim().isEmpty()){
            userMobNo.error = "Phone number must be of 10 digits"
            mobileNo = false
        } else{
            mobileNo = true
        }

        //user email id
        if(!Patterns.EMAIL_ADDRESS.matcher(userEmailId.text!!.trim()).matches()){
            userEmailId.error = "Incorrect Email Id"
            emailId = false
        } else if(userEmailId.text!!.trim().isEmpty()){
            userEmailId.error = "Can't be empty"
            emailId = false
        } else{
            userEmailId.error = null
            emailId = true
        }

        //user name
        if(userName.text!!.trim().isEmpty()){
            userName.error = "Can't be empty"
            name = false
        } else{
            userName.error = null
            name = true
        }

        //Checking all and responding back
        if(name && mobileNo && emailId && age){
            return true
        }
        return false
    }

    private fun userAgeValidation() {
        val pattern = Pattern.compile("(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)")
        if(!pattern.matcher(userAge.text).matches()){
            userAge.error = "Invalid date"
        } else{
            userAge.error = null
        }
    }

    private fun emailIdValidation() {
        if(!Patterns.EMAIL_ADDRESS.matcher(userEmailId.text!!.trim()).matches()){
            userEmailId.error = "Incorrect Email Id"
        } else if(userEmailId.text!!.trim().isEmpty()){
            userEmailId.error = "Can't be empty"
        } else{
            userEmailId.error = null
        }
    }

    fun alertDialog(){
        viewGroup = findViewById(android.R.id.content) //This is an in-built id and not made by programmer
        val dialogView = LayoutInflater.from(this).inflate(R.layout.correct_subscription_code_popup,viewGroup,false)
        builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        alertDialog= builder.create()

        //This won't allow dialog to dismiss if touched outside it's area
        alertDialog.setCanceledOnTouchOutside(false)

        //Transparent background for alert dialog
        alertDialog.window.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

        alertDialog.show()
    }

    fun datePicker(){
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        datePickerDialog = DatePickerDialog(this@PaymentActivity,DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

            //Setting date to edit text
            userAge.setText((StringBuilder().append(dayOfMonth).append("/").append(month+1).append("/").append(year)).toString())
        },year,month,day)

        //This will disable all the further dates
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }
}
