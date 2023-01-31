package com.example.classmonitor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.FragmentNavigatorExtras

class ExamActivity : AppCompatActivity() {
    // use appModel from MainActivity
    companion object {
        val app = MainActivity.app
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // get allowedCalculators from intent
        intent.getStringArrayListExtra("allowedCalculators")?.let { app.setAllowedCalculators(it) }

        // print allowed calculators
        println("Allowed calculators: ${app.getAllowedCalculators()}")

        setContentView(R.layout.activity_exam)
    }


}