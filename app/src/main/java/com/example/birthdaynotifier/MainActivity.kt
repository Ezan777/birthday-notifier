package com.example.birthdaynotifier

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.work.*
import java.time.*
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    @SuppressLint("Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        BirthdayNotification.createBirthdayNotificationChannel(this)

        val permissions = arrayOf(
            android.Manifest.permission.READ_CALENDAR,
            android.Manifest.permission.WRITE_CALENDAR
        )

        for (permission in permissions) {
            var hasPermission: Boolean = true
            hasPermission = hasPermission && ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PERMISSION_GRANTED

            if (!hasPermission) {
                ActivityCompat.requestPermissions(this, permissions, 42)
            }
        }

        val settings = applicationContext.getSharedPreferences(
            getString(R.string.default_preferences),
            Context.MODE_PRIVATE
        )

        Log.d("setNoti", settings.getStringSet(
            applicationContext.getString(R.string.sent_notifications_key),
            mutableSetOf<String>()).toString())

        var calendarName: String =
            settings.getString(getString(R.string.calendar_name_key), "").toString()

        val calendarNameInput: EditText = findViewById(R.id.calendarNameInput)
        val setCalendarButton: Button = findViewById(R.id.setCalendarButton)
        val currentCalendarName: TextView = findViewById(R.id.currentCalendarName)
        currentCalendarName.text = calendarName

        setCalendarButton.setOnClickListener {
            calendarName = calendarNameInput.text.toString().trim()
            calendarNameInput.setText("")

            if (CalendarService.getCalendarIdByName(this, calendarName) != null) {
                currentCalendarName.text = calendarName

                val notifierRequest =
                    PeriodicWorkRequestBuilder<NotifierWorker>(1, TimeUnit.HOURS).setInputData(
                        workDataOf(
                            getString(R.string.calendar_name_key) to calendarName,
                        )
                    ).build()

                WorkManager.getInstance(this).enqueue(notifierRequest)

                settings.edit()
                    .putString(getString(R.string.calendar_name_key), calendarName.toString())
                    .apply()
            } else {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.calendar_not_found),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}