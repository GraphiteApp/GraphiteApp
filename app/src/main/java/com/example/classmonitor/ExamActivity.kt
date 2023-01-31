package com.example.classmonitor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ExamActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam)

        // get allowedCalculators from intent
        val allowedCalculators = intent.getStringArrayListExtra("allowedCalculators")

        println("allowedCalculators: ")
        println(allowedCalculators)
    }
}