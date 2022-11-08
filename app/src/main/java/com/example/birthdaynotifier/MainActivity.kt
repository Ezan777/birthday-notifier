package com.example.birthdaynotifier

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.PopupMenu
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
            var hasPermission = true
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

        var calendarName: String? =
            settings.getString(getString(R.string.calendar_name_key), null).toString()

        val chooseCalendarButton: Button = findViewById(R.id.chooseCalendarButton)
        chooseCalendarButton.text = calendarName ?: getString(R.string.calendar_list_label)

        val popupMenu = PopupMenu(this, chooseCalendarButton)

        val calendarsDetails= CalendarService.getAllCalendars(this)

        if(calendarsDetails.isNotEmpty()) {
            for(calendar in calendarsDetails) {
                popupMenu.menu.add(calendar["displayName"])
            }
        }

        chooseCalendarButton.setOnClickListener {
            popupMenu.show()
        }

        popupMenu.setOnMenuItemClickListener {
            WorkManager.getInstance(this).cancelAllWork()

            calendarName = it.title.toString()
            chooseCalendarButton.text = calendarName ?: getString(R.string.calendar_list_label)
            popupMenu.menu.close()

            val notifierRequest =
                PeriodicWorkRequestBuilder<NotifierWorker>(15, TimeUnit.MINUTES).setInputData(
                    workDataOf(
                        getString(R.string.calendar_name_key) to calendarName,
                    )
                ).build()

            WorkManager.getInstance(this).enqueue(notifierRequest)

            settings.edit()
                .putString(getString(R.string.calendar_name_key), calendarName.toString())
                .apply()

            true
        }
    }
}