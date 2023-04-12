package com.example.graphite

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.example.graphite.databinding.ActivityMainBinding
import okhttp3.Call
import okhttp3.OkHttpClient
import java.io.IOException

class AppModel: ViewModel() {
    // calculator url
    private var calculatorURL = "https://www.desmos.com/calculator"

    fun getCalculatorURL(): String {
        return calculatorURL
    }

    fun setCalculatorURL(newURL: String) {
        calculatorURL = newURL
    }

    // arraylist of allowed calculators
    private var allowedCalculators = ArrayList<Map<String, String>>()

    fun getAllowedCalculators(): ArrayList<Map<String, String>> {
        return allowedCalculators
    }

    fun setAllowedCalculators(newAllowedCalculators: ArrayList<Map<String, String>>) {
        allowedCalculators = newAllowedCalculators
    }

    fun resetAllowedCalculators() {
        allowedCalculators = arrayListOf(
            mapOf("name" to "Basic Calculator", "url" to "https://www.desmos.com/fourfunction"),
            mapOf("name" to "Scientific Calculator", "url" to "https://www.desmos.com/scientific"),
            mapOf("name" to "Graphing Calculator", "url" to "https://www.desmos.com/calculator"),
        )
    }

    // is exam mode
    private var isExamMode = false

    fun getIsExamMode(): Boolean {
        return isExamMode
    }

    fun setIsExamMode(newIsExamMode: Boolean) {
        isExamMode = newIsExamMode
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

        // reset allowed calculators
        app.resetAllowedCalculators()

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
            } else {
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
                            // Get allowed calculators
                            val allowedCalculators = arrayListOf<Map<String, String>>()

                            val allowedCalculatorRequest = okhttp3.Request.Builder()
                                .url(apiURL + "get_resources" + "?class_code=" + teacherCode)
                                .build()

                            client.newCall(allowedCalculatorRequest).enqueue(object: okhttp3.Callback {
                                override fun onFailure(call: Call, e: IOException) {
                                    TODO("Not yet implemented")
                                }

                                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                                    if (response.code == 200) {
                                        val responseBody = response.body?.string()
                                        val json = org.json.JSONObject(responseBody)

                                        val allowedResources = json.getJSONArray("allowed_resources")

                                        for (i in 0 until allowedResources.length()) {
                                            val resource = allowedResources.getJSONObject(i)
                                            if (resource.getBoolean("isAllowed")) {
                                                val name = resource.getString("name")
                                                val url = resource.getString("url")

                                                allowedCalculators.add(mapOf("name" to name, "url" to url))
                                            }
                                        }

                                        // move to first fragment, pass in allowedCalculators
                                        val bundle = Bundle();
                                        bundle.putSerializable("allowedCalculators", allowedCalculators)
                                        val intent = android.content.Intent(this@MainActivity, ExamActivity::class.java)
                                        intent.putExtras(bundle)
                                        startActivity(intent)
                                    }
                                }
                            })
                        } else {
                            println(response)
                            Snackbar.make(binding.root, "Error connecting to server", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show()
                        }
                    }
                })
            }

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
