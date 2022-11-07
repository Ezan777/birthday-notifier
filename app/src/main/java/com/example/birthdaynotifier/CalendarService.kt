package com.example.birthdaynotifier

import android.content.Context
import android.database.Cursor
import android.provider.CalendarContract
import android.util.Log
import java.time.LocalDate
import java.time.ZoneId

class CalendarService {
    companion object Functions {
        fun getCalendarIdByName(context: Context, calendarName: String): String? {
            val calendarUri = CalendarContract.Calendars.CONTENT_URI
            val fields: Array<String> = arrayOf(
                CalendarContract.Calendars._ID,
            )

            val selection =
                "(${CalendarContract.Events.CALENDAR_DISPLAY_NAME} = ?)"
            val selectionArgs: Array<String> = arrayOf(
                calendarName
            )

            val contentResolver = context.contentResolver
            try {
                val cursor: Cursor? = contentResolver.query(calendarUri, fields, selection, selectionArgs, null)
                if (cursor != null) {
                    if (cursor.count > 0) {
                        var calendarId = ""
                        while (cursor.moveToNext()) {
                            calendarId = cursor.getString(0)
                        }
                        cursor.close()
                        return calendarId
                    }
                }
            } catch (exception: java.lang.Exception) {
                Log.d("exception", exception.toString())
            }

            return null
        }

        fun getTodayBirthdayEvents(context: Context, birthdayCalendarId: String): ArrayList<Event> {
            val eventsUri =
                CalendarContract.Events.CONTENT_URI
            val fields: Array<String> = arrayOf(
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.DESCRIPTION
            )

            val selection =
                "((${CalendarContract.Events.CALENDAR_ID} = ?) AND (${CalendarContract.Events.DTSTART} = ?))"
            val selectionArgs: Array<String> = arrayOf(
                birthdayCalendarId,
                LocalDate.now().atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
                    .toString()
            )

            val contentResolver = context.contentResolver
            try {
                val cursor: Cursor? =
                    contentResolver.query(eventsUri, fields, selection, selectionArgs, null)
                if (cursor != null) {
                    if (cursor.count > 0) {
                        val todayBirthdays = ArrayList<Event>()
                        while (cursor.moveToNext()) {
                            val title: String = cursor.getString(0)
                            val start: String = cursor.getString(1)
                            val end: String = cursor.getString(2)
                            val description = cursor.getString(3)

                            todayBirthdays.add(Event(title, start, end, description))
                        }
                        cursor.close()
                        return todayBirthdays
                    }
                }
            } catch (exception: java.lang.Exception) {
                Log.d("exception", exception.toString())
            }

            return ArrayList<Event>()
        }
    }
}