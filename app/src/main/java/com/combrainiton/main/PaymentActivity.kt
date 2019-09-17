package com.combrainiton.main

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.combrainiton.R

class PaymentActivity : AppCompatActivity() {

    lateinit var paymentButton: Button
    lateinit var viewGroup: ViewGroup
    lateinit var builder: AlertDialog.Builder
    lateinit var alertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        paymentButton = findViewById(R.id.activity_payment_paymentButton)

        viewGroup = findViewById(android.R.id.content)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.correct_subscription_code_popup,viewGroup,false)
        builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        alertDialog= builder.create()

        //This won't allow dialog to dismiss if touched outside it's area
        alertDialog.setCanceledOnTouchOutside(false)

        //Making background transparent of alertdialog
        alertDialog.window.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

        paymentButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                alertDialog.show()
            }

        })
    }
}
