package com.combrainiton.normalQuiz

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.combrainiton.BuildConfig
import com.combrainiton.R
import com.combrainiton.managers.NormalQuizManagement
import com.combrainiton.models.GetNormalQuizScoreResponceModel
import com.combrainiton.models.ObjectQuizResult
import com.combrainiton.utils.AppProgressDialog
import com.combrainiton.utils.AppSharedPreference
import com.combrainiton.utils.NetworkHandler
import kotlinx.android.synthetic.main.activity_normal_quiz_result.*
import java.io.File
import java.io.FileOutputStream
import android.Manifest.permission
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.os.Build
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.graphics.Canvas
import android.nfc.Tag
import android.os.StrictMode
import android.util.Log
import java.io.ByteArrayOutputStream


class ActivityNormalQuizResult : AppCompatActivity() {

    private lateinit var allData: ArrayList<ObjectQuizResult>

    private var quizId: Int = 0
    private var quizScore: String? = null
    private var playerName: String? = null
    private var quizName: String? = null
    private var userName: String? = null
    private var imageFileToShare: File? = null


    private val TAG: String = "ActivityNormalQuizResult"    // to check the log


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_normal_quiz_result)
        initViews()
    }

    override fun onResume() {
        super.onResume()
        challange_view.visibility = View.INVISIBLE
    }

    @SuppressLint("SetTextI18n", "LongLogTag")
    private fun initViews() {

        challange_view.isDrawingCacheEnabled = true
        top_view.isDrawingCacheEnabled = true

        //get score data
        val scoreData = intent.getSerializableExtra("scoreData") as GetNormalQuizScoreResponceModel?
        quizScore = String.format("%.2f", scoreData!!.total_score)

        //set score data
        tvTotalScore.text = String.format("%.2f", scoreData!!.total_score)

        //set accuracy
        val accuracy = (100 * scoreData.correct_answers) / scoreData.total_questions
        tvAccuracy.text = "$accuracy%"

        //play sound if sound is enabled
        if (AppSharedPreference(this@ActivityNormalQuizResult).getBoolean("sound")) {
            val mp = MediaPlayer.create(applicationContext, R.raw.final_leaderboard)
            mp.start()
        }

        //set on click listener to home button
        normal_quiz_home_button.setOnClickListener {
            onBackPressed()
        }

        //set on click listener to play again button
        normal_quiz_play_again_button.setOnClickListener {
            val quizId: Int = intent.getIntExtra("quizId", 0)
            val mDialog = AppProgressDialog(applicationContext)
            mDialog.show()
            NormalQuizManagement(applicationContext, this, mDialog).getQuizDetail(quizId)
        }

    }

    fun shareScore(view: View) {
        if(isReadStoragePermissionGranted() && isWriteStoragePermissionGranted() && forceToAccessPath()) {
            //challange_view.visibility = View.VISIBLE

            quizId = intent.getIntExtra("quizId", 0)
            quizName = intent.getStringExtra("quizName")
            userName = AppSharedPreference(this@ActivityNormalQuizResult).getString("name")

            challange_quiz_name.text = quizName
            challange_score.text = quizScore
            challange_user_name.text = userName

//            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
//            val canvas = Canvas(bitmap)

            val mBitmap = challange_view.drawingCache
            try {
                val path = Environment.getExternalStorageDirectory().toString() + "/$quizId.png"
                val outputStream = FileOutputStream(File(path))
                mBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
                outputStream.close()
            } catch (exception: Exception) {
                Toast.makeText(this, exception.message + "Could not share score", Toast.LENGTH_SHORT).show()
            }

            val imagePath : String = Environment.getExternalStorageDirectory().toString() + "/$quizId.png"
            imageFileToShare = File(imagePath)
            val uri = Uri.fromFile(imageFileToShare)
            try {
                val share = Intent(Intent.ACTION_SEND)
                share.type = "image/*"
                share.putExtra(Intent.EXTRA_STREAM, uri)
                share.putExtra(Intent.EXTRA_TEXT, "You've been challenged to play a quiz '$quizName'.\nUse PIN : N$quizId to accept the challenge.\n\n" + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n")
                startActivity(Intent.createChooser(share, "Challenge your friend"))

            } catch (e : Exception){
                Toast.makeText(this, e.message + "Could not share score", Toast.LENGTH_SHORT).show()
            }

        }else {
            Toast.makeText(this, "Please give permission to store score image ", Toast.LENGTH_SHORT).show()
        }
    }


    // to delete the stored image when activity is destroyed
    @SuppressLint("LongLogTag")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Because app crashes sometimes without the try->catch
        try {
            // if file exists in memory
            if (imageFileToShare!!.exists()) {
                imageFileToShare!!.delete()
                Log.e(TAG,"image deleted :- $imageFileToShare")
            }
            else{
                Toast.makeText(this, "image not deleted", Toast.LENGTH_SHORT).show()
            }
        } catch (e : Exception) {
            Log.e(TAG,e.toString())
        }
    }

    // forcefully allowes devices to give access to path to the sotred image
    fun forceToAccessPath() : Boolean {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                m.invoke(null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return true
        } else {
            Toast.makeText(this@ActivityNormalQuizResult, " Problem getting the image ",Toast.LENGTH_SHORT).show()
            return false
        }
    }

    @SuppressLint("LongLogTag")
    override fun onBackPressed() {
        // Because app crashes sometimes without the try->catch
        try {
            // if file exists in memory
            if (imageFileToShare!!.exists()) {
                imageFileToShare!!.delete()
                Log.e(TAG,"image deleted :- $imageFileToShare")
            }
            else{
                Toast.makeText(this, "image not deleted", Toast.LENGTH_SHORT).show()
            }
        } catch (e : Exception) {
            Log.e(TAG,e.toString())
        }

        //here we have to call home page api
        if (NetworkHandler(this@ActivityNormalQuizResult).isNetworkAvailable()) {
            val mDialog = AppProgressDialog(this@ActivityNormalQuizResult)
            mDialog.show()
            NormalQuizManagement(this@ActivityNormalQuizResult, this@ActivityNormalQuizResult, mDialog).getAllQuiz()
        } else {
            Toast.makeText(this@ActivityNormalQuizResult, resources.getString(R.string.error_network_issue), Toast.LENGTH_LONG).show()
        }
    }

    fun isReadStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                true
            } else {

                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 3)
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            true
        }
    }

    fun isWriteStoragePermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            return if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                true
            } else {


                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        2
                )
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation

            return true
        }
    }

}
