package com.combrainiton.main

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.combrainiton.R
import kotlinx.android.synthetic.main.activity_plan_select.*


class ActivityPlanSelect : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_select)
        cardone_bottomtext_planselect.setText(Html.fromHtml("<b>₹ 299 </b><br> ₹99/mon")).toString()
        cardtwo_bottomtext_planselect.setText(Html.fromHtml("<b>₹ 499 </b><br> ₹39/mon")).toString()
//
//        val drawable4 = BadgeDrawable.Builder()
//                .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
//                .badgeColor(0xFF63C599.toInt())
//                .text1("60%")
//                .text2("OFF")
//                .textSize(40F)
//                .padding(5F, 5F, 5F, 1F, 5F)
//                .strokeWidth(2)
//                .build()
//
//
//        val spannableString = SpannableString(TextUtils.concat(
//                "12\nMONTHS",
//                drawable4.toSpannable()
//        ))
//
//        twelvemonths_text.setText(spannableString)

//        secondcard_plan_select.setBadgeValue
        continue_button_plan_select.setOnClickListener {
            startActivity(Intent(this,PaymentActivity::class.java))
        }
    }
}
