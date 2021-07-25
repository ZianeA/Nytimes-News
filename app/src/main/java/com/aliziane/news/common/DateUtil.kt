package com.aliziane.news

import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

private val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")
    .withLocale(Locale.US)
    .withZone(ZoneId.systemDefault())

fun Instant.format(): String = formatter.format(this.toJavaInstant())