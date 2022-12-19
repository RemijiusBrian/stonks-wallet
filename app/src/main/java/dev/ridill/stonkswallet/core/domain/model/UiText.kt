package dev.ridill.stonkswallet.core.domain.model

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class UiText {
    data class DynamicText(val message: String) : UiText()
    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any
    ) : UiText()

    fun asString(context: Context): String = when (this) {
        is DynamicText -> message
        is StringResource -> context.getString(resId, *args)
    }

    @Composable
    fun asString(): String = when (this) {
        is DynamicText -> message
        is StringResource -> stringResource(resId, args)
    }
}