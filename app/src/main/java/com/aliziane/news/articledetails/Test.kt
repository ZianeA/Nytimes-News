package com.aliziane.news.articledetails

import androidx.lifecycle.MutableLiveData
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toLocalDateTime
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*
import kotlin.math.abs

fun main() {
    val instant = Clock.System.now()
    val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")
        .withLocale(Locale.US)
        .withZone(ZoneId.systemDefault())

    val num = 100
    val num2 = 1000
    val num3 = 1100
    val num4 = 1000000

    println(num.toPrettyCount() + ", " + num2.toPrettyCount() + ", " + num3.toPrettyCount() + ", " + num4.toPrettyCount())
    println(formatter.format(instant.toJavaInstant()))
    println(instant)
}

private fun Int.toPrettyCount(): String {
    val formatSymbols = DecimalFormatSymbols(Locale.getDefault())
    formatSymbols.decimalSeparator = '.'
    val formatter = DecimalFormat("#.#", formatSymbols)

    return when {
        abs(this / 1000000) >= 1 -> formatter.format(this / 1000000.0) + "m"
        abs(this / 1000) >= 1 -> formatter.format(this / 1000.0) + "k"
        else -> this.toString()
    }
}