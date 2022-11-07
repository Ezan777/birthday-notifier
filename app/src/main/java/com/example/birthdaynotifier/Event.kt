package com.example.birthdaynotifier

import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

class Event(var title: String, var startDateTimeStamp: String, var endDateTimeStamp: String, var description: String) {

    fun getStartDate(): String {
        return DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli(startDateTimeStamp.toLong()))
    }

    fun getEndDate(): String {
        return DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli(endDateTimeStamp.toLong()))
    }

    override fun toString(): String {
        return "$title starts at ${getStartDate()} and ends  at ${getEndDate()}"
    }
}