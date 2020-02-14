package com.francesco.montano.notify

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import android.content.Context.MODE_PRIVATE
import android.R.id.edit
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent.getActivity
import android.content.Context
import com.google.firebase.iid.FirebaseInstanceId
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Build
import android.util.JsonToken
import android.util.Log
import android.widget.TextView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException


class MainActivity : AppCompatActivity() {

    private lateinit var functions: FirebaseFunctions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        createNotificationChannel()

        val fms = MyFirebaseMessagingService()

        val sharedPref = this?.getPreferences(Context.MODE_PRIVATE)
        val oldId = sharedPref.getString("ID", "defaultValue")
        Log.e("before", oldId)
        if(oldId == "defaultValue"){

            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this) { instanceIdResult ->
                val newToken = instanceIdResult.token
                Log.e("newToken", newToken)
                this.getPreferences(Context.MODE_PRIVATE).edit().putString("fb", newToken).apply()

                functions = FirebaseFunctions.getInstance()

                addToken(newToken)
                        .addOnCompleteListener(OnCompleteListener { task ->

                            if (!task.isSuccessful) {
                                val e = task.exception
                                Log.e("getting the new id", e.toString())
                            } else {
                                Log.e("getting the new id", task.result)

                                val res = stringToString(task.result)

                                val sharedPref = this?.getPreferences(Context.MODE_PRIVATE)
                                with (sharedPref.edit()) {
                                    putString("ID", res)
                                    commit()
                                }

                                displayId(res)

                            }

                        })

            }

        }

        else{
            val oldId = stringToString(sharedPref.getString("ID", "defaultValue"))
            Log.e("old Id", oldId)

            displayId(oldId)
        }

        FirebaseInstanceId.getInstance().instanceId.addOnFailureListener(this) {instanceIdResult ->
            Log.e("newToken", "error")
        }

    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notify"
            val descriptionText = "Notification channel for Notify app"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("Notify", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun addToken(token: String): Task<String> {
        // Create the arguments to the callable function.

        val data : HashMap<String, String>  = hashMapOf("token" to token)

        Log.e("before calling", "before calling")
        return functions
                .getHttpsCallable("addToken")
                .call(data)
                .continueWith { task ->
                    // This continuation runs on either success or failure, but if the task
                    // has failed then result will throw an Exception which will be
                    // propagated down.
                    val result = task.result?.data as String
                    result

                }

    }

    private fun displayId(id:String){
        val textView: TextView = findViewById(R.id.id) as TextView
        textView.text = id

    }

    private fun stringToString(str:String?): String{
        return str ?: ""
    }



    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
