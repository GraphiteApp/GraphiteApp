package com.example.classmonitor

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.preference.PreferenceManager
import okhttp3.OkHttpClient

class ExamActivity : AppCompatActivity() {
    // use appModel from MainActivity
    companion object {
        val app = MainActivity.app
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // get allowedCalculators from intent
        intent.getStringArrayListExtra("allowedCalculators")?.let { app.setAllowedCalculators(it) }

        // enable exam mode
        app.setIsExamMode(true)

        setContentView(R.layout.activity_exam)
    }

    fun leaveExam() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val teacherCode = sharedPref.getString("teacher_code", "").toString().uppercase()
        val username = sharedPref.getString("pref_key_name", "").toString()
        val apiURL = sharedPref.getString("api_url", "").toString()

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