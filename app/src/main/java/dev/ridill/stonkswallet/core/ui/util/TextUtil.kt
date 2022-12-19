package dev.ridill.stonkswallet.core.ui.util

import android.icu.text.CompactDecimalFormat
import dev.ridill.stonkswallet.core.util.ifNaN
import java.text.NumberFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

object TextUtil {
    private val numberFormat = NumberFormat.getNumberInstance()

    private fun formatNumber(number: Double): String =
        numberFormat.format(number)

    private fun formatNumber(number: Long): String =
        numberFormat.format(number)

    fun compactFormatAmountWithCurrency(
        amount: Double,
        compactStyle: CompactDecimalFormat.CompactStyle = CompactDecimalFormat.CompactStyle.SHORT
    ): String = buildString {
        if (amount < 0) append("-")
        append(currencySymbol)
        append(
            CompactDecimalFormat
                .getInstance(Locale.getDefault(), compactStyle)
                .format(abs(amount))
        )
    }

    fun formatAmountWithCurrency(amount: Double): String = buildString {
        if (amount < 0) append("-")
        append(currencySymbol)
        append(formatNumber(abs(amount)))
    }

    fun formatAmountWithCurrency(amount: Long): String = buildString {
        if (amount < 0) append("-")
        append(currencySymbol)
        append(formatNumber(abs(amount)))
    }

    fun formatPercent(percent: Float): String = buildString {
        if (percent < 0) append("-")
        append((abs(percent.ifNaN { 0f }) * 100).roundToInt())
        append("%")
    }

    val currencySymbol: String
        get() = Currency.getInstance(Locale.getDefault()).symbol
}