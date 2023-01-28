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
import androidx.preference.PreferenceManager
import com.example.classmonitor.databinding.ActivityMainBinding
import okhttp3.OkHttpClient

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

        // onclick start exam, check if teacherCode and username in settings are set
        menu.findItem(R.id.action_exam).setOnMenuItemClickListener {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
            val teacherCode = sharedPref.getString("teacher_code", "").toString().uppercase()
            val username = sharedPref.getString("pref_key_name", "").toString()
            val apiURL = sharedPref.getString("api_url", "").toString()
            if (teacherCode == "" || username == "") {
                Snackbar.make(binding.root, "Please set class code and username in settings", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
                true
            }

            // http post request to server cm.thearchons.xyz/api/start_exam
            // if response is 200, move to exam activity
            // else, show error message

            val url = apiURL + "join_exam"

            // debug log url
            println(url)
            val client = OkHttpClient()
            val request = okhttp3.Request.Builder()
                .url(url)
                .post(okhttp3.FormBody.Builder()
                    .add("class_code", teacherCode)
                    .add("username", username)
                    .build())
                .build()

            client.newCall(request).enqueue(object: okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                    println(e)
                    Snackbar.make(binding.root, "Error connecting to server", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    if (response.code == 200) {
                        // TODO move to exam activity
                        //val intent = android.content.Intent(this@MainActivity, Exam::class.java)
                        //startActivity(intent)
                    } else {
                        println(response)
                        Snackbar.make(binding.root, "Error connecting to server", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                    }
                }
            })

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

private fun MenuItem.setOnMenuItemClickListener(function: (MenuItem) -> Unit) {

}
