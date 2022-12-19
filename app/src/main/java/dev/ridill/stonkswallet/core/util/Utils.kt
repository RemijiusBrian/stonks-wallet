package dev.ridill.stonkswallet.core.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.shouldShowRationale

val <T> T.exhaustive: T
    get() = this

inline fun <T> tryOrNull(tryBlock: () -> T): T? = try {
    tryBlock()
} catch (t: Throwable) {
    t.printStackTrace()
    null
}

inline fun Float.ifNaN(defaultValue: () -> Float) = if (!isNaN()) this else defaultValue()

//inline fun Double.ifNaN(defaultValue: () -> Float) = if (!isNaN()) this else defaultValue()

fun Double?.orZero(): Double = this ?: 0.0

//fun Float?.orZero(): Float = this ?: 0f

fun String.toDoubleOrZero(): Double = this.toDoubleOrNull().orZero()

//fun <T> T.isAnyOf(vararg items: T): Boolean = this in items

fun PermissionStatus.isPermanentlyDenied(): Boolean =
    !shouldShowRationale && this is PermissionStatus.Denied

@Composable
fun Color.onVariant(): Color {
    val perceivedBrightness = (red * 0.299) + (green * 0.587) + (blue * 0.114) * ((1 - alpha) * 255)
    return if (perceivedBrightness > 0.186) Color.Black else Color.White
}

inline fun log(stmt: () -> Any?) {
    println("AppDebug: ${stmt()}")
}

fun log(vararg data: Any?) {
    println("AppDebug: ${data.joinToString(" - ")}")
}