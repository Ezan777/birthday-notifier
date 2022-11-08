package com.example.birthdaynotifier

import android.content.Context
import android.content.SharedPreferences
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.time.LocalDate

class NotifierWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    private val settings: SharedPreferences = appContext.getSharedPreferences(
        applicationContext.getString(R.string.default_preferences),
        Context.MODE_PRIVATE
    )

    private fun checkForBirthdays(context: Context, calendarName: String) {
        val calendarId = CalendarService.getCalendarIdByName(context, calendarName)
        val sentNotifications: MutableSet<String> =
            settings.getStringSet(
                applicationContext.getString(R.string.sent_notifications_key),
                mutableSetOf<String>()
            ) as MutableSet<String>

        if (calendarId != null) {
            // The calendar exists
            val birthdays = CalendarService.getTodayBirthdayEvents(context, calendarId)

            if (birthdays.isNotEmpty()) {
                for (birthday in birthdays) {
                    if (birthday.title !in sentNotifications) {
                        BirthdayNotification.sendBirthdayNotification(context, birthday)
                        sentNotifications.add(birthday.title)
                    }
                }
                settings.edit().putStringSet(
                    applicationContext.getString(R.string.sent_notifications_key),
                    sentNotifications
                ).apply()
            }
        } else {
            throw Exception(context.getString(R.string.calendar_not_found))
        }
    }

    override fun doWork(): Result {
        try {
            val calendarName =
                inputData.getString(applicationContext.getString(R.string.calendar_name_key))
                    ?: return Result.failure()

            if (settings.getString(
                    applicationContext.getString(R.string.last_sent_key),
                    ""
                ) != LocalDate.now().toString()
            ) {
                settings.edit().putString(
                    applicationContext.getString(R.string.last_sent_key),
                    LocalDate.now().toString()
                ).apply()
                settings.edit().putStringSet(
                    applicationContext.getString(R.string.sent_notifications_key),
                    mutableSetOf<String>()
                ).apply()
            }

            checkForBirthdays(applicationContext, calendarName)
            return Result.success()
        } catch (exception: Exception) {
            return Result.failure()
        }
    }
}