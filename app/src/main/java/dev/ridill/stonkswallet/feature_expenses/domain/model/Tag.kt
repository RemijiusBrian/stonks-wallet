package dev.ridill.stonkswallet.feature_expenses.domain.model

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import dev.ridill.stonkswallet.core.ui.theme.MoneyGreen30
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tag(
    val name: String,
    val colorCode: Int
) : Parcelable {

    val color: Color
        get() = Color(colorCode)

    companion object {
        val Untagged = Tag(
            name = "Untagged",
            colorCode = MoneyGreen30.toArgb()
        )
    }
}