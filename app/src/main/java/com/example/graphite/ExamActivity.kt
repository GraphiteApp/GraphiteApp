package com.example.graphite

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import okhttp3.*
import java.io.IOException

class ExamActivity : AppCompatActivity() {
    // use appModel from MainActivity
    companion object {
        val app = MainActivity.app
    }


    // when activity is moved to the background
    override fun onPause() {
        super.onPause()
        leaveExam()
    }

    // when app is closed
    override fun onDestroy() {
        super.onDestroy()
        leaveExam()
    }

    // when the back button is pressed
    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Leave Exam")
            .setMessage("Are you sure you want to leave the exam?")
            .setPositiveButton("Yes") { _, _ ->
                leaveExam()
                super.onBackPressed()
            }
            .setNegativeButton("No", null)
            .show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // get allowedCalculators from intent
        intent.getSerializableExtra("allowedCalculators")?.let {
            app.setAllowedCalculators(it as ArrayList<Map<String, String>>)
        }

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
        val request = Request.Builder()
            .url(apiURL + "leave_exam")
            .post(
                FormBody.Builder()
                    .add("class_code", teacherCode)
                    .add("username", username)
                    .build()
            )
            .build()

        // send request
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                AlertDialog.Builder(this@ExamActivity)
                    .setTitle("Error")
                    .setMessage("Could not leave exam. Please check your internet connection.")
                    .setPositiveButton("OK") { dialog, which -> }
                    .show()
            }

            override fun onResponse(call: Call, response: Response) {
                // disable exam mode
                app.setIsExamMode(false)

                // enable all calculators
                app.setAllowedCalculators(
                    arrayListOf(
                        mapOf("name" to "Graphing Calculator", "id" to "graphing"),
                        mapOf("name" to "Scientific Calculator", "id" to "scientific"),
                        mapOf("name" to "Basic Calculator", "id" to "basic")
                    )
                )

                // finish activity
                finish()
            }
        })
    }
}