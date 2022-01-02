package me.ckho.scriptscompose.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.math.BigInteger
import java.security.MessageDigest
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*


val JSONMapper: ObjectMapper = ObjectMapper().registerModule(
    KotlinModule.Builder()
        .withReflectionCacheSize(512)
        .configure(KotlinFeature.NullToEmptyCollection, false)
        .configure(KotlinFeature.NullToEmptyMap, false)
        .configure(KotlinFeature.NullIsSameAsDefault, false)
        .configure(KotlinFeature.SingletonSupport, false)
        .configure(KotlinFeature.StrictNullChecks, false)
        .build()
)

fun generateUIDForTask(input: String): String {
    val md: MessageDigest = MessageDigest.getInstance("MD5")
    val messageDigest: ByteArray = md.digest(input.toByteArray())
    val no = BigInteger(1, messageDigest)
    var hashtext = no.toString(16)
    while (hashtext.length < 32) {
        hashtext = "0$hashtext"
    }
    return hashtext
}

fun string2instant(datetime: String): Instant {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val temporalAccessor = formatter.parse(datetime)
    val localDateTime = LocalDateTime.from(temporalAccessor)
    val zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault())
    return Instant.from(zonedDateTime)
}

fun string2date(datetime: String): Date {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return Timestamp.valueOf(LocalDateTime.parse(datetime, formatter))
}

fun timestamp2date(timestamp: Long): Date{
    return Date(Timestamp(timestamp).time)
}

fun convertToLocalDateTimeViaInstant(dateToConvert: Date): LocalDateTime {
    return dateToConvert.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
}