package com.combrainiton.authentication

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.combrainiton.R
import com.combrainiton.managers.NormalQuizManagement
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.AppSharedPreference
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import kotlinx.android.synthetic.main.activity_welcome_user.*

class ActivityWelcomeUser : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_user)
        initViews()
        methodWithPermissions()

    }
    // fun to get all the required permissions
    fun methodWithPermissions() = runWithPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_WIFI_STATE) {
        Log.i("permissions","permissions granted")
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
