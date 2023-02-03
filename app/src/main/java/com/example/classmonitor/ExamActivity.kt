package com.example.classmonitor

import android.content.Context
import android.content.res.Resources
import android.hardware.display.DisplayManager
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import okhttp3.OkHttpClient

class ExamActivity : AppCompatActivity() {
    // use appModel from MainActivity
    companion object {
        val app = MainActivity.app
    }

    var mMediaRecorder = MediaRecorder()

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // get allowedCalculators from intent
        intent.getStringArrayListExtra("allowedCalculators")?.let { app.setAllowedCalculators(it) }

        // enable exam mode
        app.setIsExamMode(true)

        setContentView(R.layout.activity_exam)

        // start video recording
        recorder(true)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun recorder(isStart: Boolean) {
        if (isStart) {
            // start video recording

            // get media projection manager
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            val width = Resources.getSystem().displayMetrics.widthPixels
            val height = Resources.getSystem().displayMetrics.heightPixels

            // print width and height
            println("Width: $width")
            println("Height: $height")

            val context = this

            mMediaRecorder = MediaRecorder(context)

            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE)

            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)

            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264)

            mMediaRecorder.setVideoFrameRate(15)

            mMediaRecorder.setVideoSize(width, height)

            // get media path
            val mediaPath = getExternalFilesDir(null)?.absolutePath

            mMediaRecorder.setOutputFile("$mediaPath" + "test.m4e")

            println("Media path: $mediaPath")

            mMediaRecorder.prepare()

            mMediaRecorder.start()
        } else {
            // stop video recording
            mMediaRecorder.stop()
            mMediaRecorder.release()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun leaveExam() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val teacherCode = sharedPref.getString("teacher_code", "").toString().uppercase()
        val username = sharedPref.getString("pref_key_name", "").toString()
        val apiURL = sharedPref.getString("api_url", "").toString()

        // stop video recording
        recorder(false)

        // send POST request to server
        val client = OkHttpClient()
        val request = okhttp3.Request.Builder()
            .url("$apiURL" + "leave_exam")
            .post(
                okhttp3.FormBody.Builder()
                    .add("class_code", teacherCode)
                    .add("username", username)
                    .build()
            )
            .build()

        // send request
        client.newCall(request).enqueue(object: okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                AlertDialog.Builder(this@ExamActivity)
                    .setTitle("Error")
                    .setMessage("Could not leave exam. Please check your internet connection.")
                    .setPositiveButton("OK") { dialog, which -> }
                    .show()
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                // disable exam mode
                app.setIsExamMode(false)

                // finish activity
                finish()
            }
        })
    }
}