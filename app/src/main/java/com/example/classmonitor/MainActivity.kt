package com.example.classmonitor

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModel
import com.example.classmonitor.databinding.ActivityMainBinding

class AppModel: ViewModel() {
    private var calculatorURL = "https://www.desmos.com/calculator"

    fun getCalculatorURL(): String {
        return calculatorURL
    }

    fun setCalculatorURL(newURL: String) {
        calculatorURL = newURL
    }
}

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    // appModel
    companion object {
        val app = AppModel()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)

        // onclick settings, move to settings activity
        menu.findItem(R.id.action_settings).setOnMenuItemClickListener {
            val intent = android.content.Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            true
        }

        // onclick exam, move to exam activity
        menu.findItem(R.id.action_exam).setOnMenuItemClickListener {
            val intent = android.content.Intent(this, ExamActivity::class.java)
            startActivity(intent)
            true
        }


        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}