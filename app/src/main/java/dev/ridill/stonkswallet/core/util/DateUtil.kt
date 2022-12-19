package dev.ridill.stonkswallet.core.util

import androidx.annotation.StringRes
import dev.ridill.stonkswallet.R
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.*

object DateUtil {

    fun currentEpochMillis(): Long = Instant.now().toEpochMilli()

    fun currentDate(): LocalDate = LocalDate.now()

    fun currentDateTime(): LocalDateTime = LocalDateTime.now()

    fun parse(dateString: String, pattern: String): LocalDateTime? = tryOrNull {
        val formatter = valueFormatter(pattern)
        LocalDateTime.parse(dateString, formatter)
    }

    private fun valueFormatter(pattern: String): DateTimeFormatter = DateTimeFormatterBuilder()
        .append(DateTimeFormatter.ofPattern(pattern))
        .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
        .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
        .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
        .toFormatter(Locale.getDefault())
}

object DatePatterns {
    const val DD_MM_YYYY = "dd-MM-yyyy"
    const val LONG_DAY_NAME_WITH_DAY_NUMBER = "EEEE, dd"
    const val SHORT_DAY_NAME_WITH_DAY_NUMBER = "EEE, dd"
    const val SHORT_MONTH_NAME = "MMM"
    const val MM_HYPHEN_YYYY = "MM-yyyy"
    const val DD = "dd"
    const val DAY_SHORT_MONTH_NAME_YEAR = "dd, MMM yyyy"
    const val DAY_WITH_SHORT_MONTH_NAME = "dd, MMM"
}

fun Long.toDateTime(): LocalDateTime =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()

fun Long.toLocalDate(): LocalDate = this.toDateTime().toLocalDate()

val LocalDate.timeMillis: Long
    get() = atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

val LocalDateTime.partOfDay: Int
    @StringRes get() = when (hour) {
        in (0..11) -> R.string.hour_morning
        12 -> R.string.hour_noon
        in (13..18) -> R.string.hour_afternoon
        else -> R.string.hour_evening
    }

fun LocalDate.format(pattern: String = DatePatterns.DD_MM_YYYY): String =
    this.format(DateTimeFormatter.ofPattern(pattern))

fun LocalDate.getDayWithSuffix(
    longName: Boolean = false
): String = buildString {
    val pattern = if (longName) DatePatterns.LONG_DAY_NAME_WITH_DAY_NUMBER
    else DatePatterns.SHORT_DAY_NAME_WITH_DAY_NUMBER
    val formatted = this@getDayWithSuffix.format(pattern)
    append(formatted)
    val suffix = when (this@getDayWithSuffix.dayOfMonth % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
    append(suffix)
}