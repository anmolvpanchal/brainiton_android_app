package com.combrainiton.authentication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.combrainiton.R
import com.combrainiton.managers.NormalQuizManagement
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.AppSharedPreference
import kotlinx.android.synthetic.main.activity_welcome_user.*

class ActivityWelcomeUser : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_user)
        initViews()
    }

    private fun initViews() {
        AppSharedPreference(this@ActivityWelcomeUser).saveBoolean("sound", true)
        val name: String = "Welcome, " + AppSharedPreference(this@ActivityWelcomeUser).getString("name")
        btnLetsGo.setOnClickListener(this@ActivityWelcomeUser)
        tvWelcomeUser.text = name
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnLetsGo -> {
                val mDialog = AppProgressDialog(this@ActivityWelcomeUser)
                mDialog.show()
                NormalQuizManagement(this@ActivityWelcomeUser, this@ActivityWelcomeUser, mDialog).getAllQuiz()
            }

        }
    }

    override fun onBackPressed() {
        finish()
    }

}
