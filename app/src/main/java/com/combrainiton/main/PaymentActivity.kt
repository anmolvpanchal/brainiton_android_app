package com.combrainiton.main

import android.app.DatePickerDialog
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.combrainiton.R
import com.google.android.material.textfield.TextInputEditText
import java.lang.StringBuilder
import java.util.*

class PaymentActivity : AppCompatActivity() {

    lateinit var paymentButton: Button
    lateinit var viewGroup: ViewGroup
    lateinit var builder: AlertDialog.Builder
    lateinit var alertDialog: AlertDialog
    lateinit var userAge: TextInputEditText
    lateinit var datePickerDialog: DatePickerDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        //assigning ids
        paymentButton = findViewById(R.id.activity_payment_paymentButton)
        userAge = findViewById(R.id.activity_payment_userAge)

        //Showing datepickerdialog on focus of age(edittext)
        userAge.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                datePicker()
            }
        }

        //creating and showing alert dialog
        alertDialog()
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

        paymentButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                alertDialog.show()
            }

        })
    }

    fun datePicker(){
        datePickerDialog = DatePickerDialog(this@PaymentActivity,DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            //Setting date to edit text
            userAge.setText((StringBuilder().append(dayOfMonth).append("/").append(month+1).append("/").append(year)).toString())
        },0,0,0)

        //This will disable date of today and of the future
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }
}
